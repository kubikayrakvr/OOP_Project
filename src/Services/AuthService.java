package Services;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import GUI.LoginUser;
import GUI.RegistrationStatus;
import GUI.Role;

public class AuthService {

    private static final String FILE_PATH = "users.txt";

    // FIXED ADMIN CREDENTIALS
    private static final String FIXED_ADMIN_USER = "admin";
    private static final String FIXED_ADMIN_PASS = "admin";

    static {
        FileHelper.ensureFileExists(FILE_PATH);
    }
    
    public static LoginUser login(String username, String password) {
        if (username.equals(FIXED_ADMIN_USER) && password.equals(FIXED_ADMIN_PASS)) {
            return new LoginUser(FIXED_ADMIN_USER, FIXED_ADMIN_PASS, Role.ADMIN, "ADMIN-MASTER");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                // parts[0]=user, [1]=pass, [2]=role, [3]=id, [4]=question, [5]=answer
                if (parts.length < 4) continue; 

                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return new LoginUser(parts[0], parts[1], Role.valueOf(parts[2]), parts[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("User database not found or error reading.");
        }
        return null;
    }

    public static RegistrationStatus register(LoginUser user, String securityQuestion, String securityAnswer) {
        
        if (user.getUsername().equalsIgnoreCase(FIXED_ADMIN_USER)) return RegistrationStatus.USERNAME_EXISTS;
        if (!isValidUsername(user.getUsername())) return RegistrationStatus.INVALID_CHARACTERS;
        if (userExists(user.getUsername())) return RegistrationStatus.USERNAME_EXISTS;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(
                user.getUsername() + ";" +
                user.getPassword() + ";" +
                user.getRole() + ";" +
                user.getId() + ";" +
                securityQuestion + ";" + 
                securityAnswer
            );
            bw.newLine();
            return RegistrationStatus.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
            return RegistrationStatus.FILE_ERROR;
        }
    }

    public static String getSecurityQuestionForUser(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 6 && parts[0].equals(username)) {
                    return parts[4];
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean resetPassword(String username, String securityAnswer, String newPassword) {
        List<String> fileContent = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                
                if (parts.length >= 6 && parts[0].equals(username)) {
                    if (parts[5].equalsIgnoreCase(securityAnswer)) {
                        String newLine = parts[0] + ";" + newPassword + ";" + parts[2] + ";" + parts[3] + ";" + parts[4] + ";" + parts[5];
                        fileContent.add(newLine);
                        updated = true;
                    } 
                    else {
                        fileContent.add(line);
                        return false; 
                    }
                } else {
                    fileContent.add(line);
                }
            }
        } catch (IOException e) { return false; }

        if (updated) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (String s : fileContent) {
                    bw.write(s);
                    bw.newLine();
                }
            } 
            catch (IOException e) { return false; }
        }
        return updated;
    }
    
    public static List<LoginUser> getAllRegisteredUsers() {
        List<LoginUser> users = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 4) continue; 

                try {
                    // String to enum
                    Role role = Role.valueOf(parts[2]); 
                    
                    LoginUser u = new LoginUser(parts[0], parts[1], role, parts[3]);
                    users.add(u);
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping invalid role in file: " + parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public static boolean deleteUser(String userIdToDelete) {
        File inputFile = new File(FILE_PATH);
        List<String> linesToKeep = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 4 && parts[3].equals(userIdToDelete)) {
                    found = true;
                } else {
                    linesToKeep.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (found) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile))) {
                for (String s : linesToKeep) {
                    bw.write(s);
                    bw.newLine();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        return false;
    }
    
    private static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9]+$");
    }

    private static boolean userExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(username + ";")) return true;
            }
        } catch (IOException e) {}
        return false;
    }
}