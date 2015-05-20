package com.teamdc.stephendiniz.autoaway.classes;

import java.util.ArrayList;
import java.util.List;

public class PhoneContact
{
	private String name;
	private String number;
	private String id;

    private List<String> phoneNumbers = new ArrayList<String>();

	public PhoneContact(String name, String number, String id)	{
		this.name	= name;
		this.number	= number;
        this.phoneNumbers.add(number);
		this.id		= id;
	}

    public PhoneContact(String name, String id){
        this.name = name;
        this.id = id;
    }

	public String getName()										{ return name;			}
	public String getNumber()									{ return number;		}
	public String getId()										{ return id;			}
	
	public void setName(String name)							{ this.name = name;		}
	public void setNumber(String number)						{ this.number = number;	}
	public void setId(String id)								{ this.id = id;			}

	public void setInfo(String name, String number, String id)	{ this.name = name;
																  this.number = number;
												  				  this.id = id;			}

    public void addPhoneNumber(String phoneNumber){
        this.phoneNumbers.add(phoneNumber);
    }

    public boolean hasMultipleNumbers(){
        return this.phoneNumbers.size() > 1;
    }

    public List<Contact> splitInContacts(){
        List<Contact> contacts = new ArrayList<Contact>(this.phoneNumbers.size());

        for(String phoneNumber : phoneNumbers){

            Contact contact = new Contact(this.name, phoneNumber);
            contacts.add(contact);
        }

        return contacts;
    }

    @Override
    public String toString() {
        return name;
    }
}
