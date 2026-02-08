package TicketReservation;

public class Passenger {

    private String passengerID;
    private String name;
    private String surname;
    private ContactInfo contactInfo;

    public Passenger(String id, String name, String surname,
              String mail, String phone, String address) {

        this.passengerID = id;
        this.name = name;
        this.surname = surname;
        this.contactInfo = new ContactInfo(mail, phone, address);
    }

    public class ContactInfo {
        private String mail;
        private String phone;
        private String address;

        private ContactInfo(String mail, String phone, String address) {
            this.mail = mail;
            this.phone = phone;
            this.address = address;
        }

        @Override
        public String toString() {
            return "ContactInfo {" +
                   "mail='" + mail + '\'' +
                   ", phone='" + phone + '\'' +
                   ", address='" + address + '\'' +
                   '}';
        }
    }

    @Override
    public String toString() {
        return "Passenger {" +
               "passengerID='" + passengerID + '\'' +
               ", name='" + name + '\'' +
               ", surname='" + surname + '\'' +
               ", contactInfo=" + contactInfo +
               '}';
    }

	public String getPassengerID() {
		return passengerID;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}
    
    
}
