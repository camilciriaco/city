package app.appurservice.sigin;

/**
 * Created by RVN-TECH on 8/1/2016.
 */
public class RegisterData {

    //private variables
    int _id;
    String username;
    String email_id;
    String mobile_number;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    String password;

    // Empty constructor
    public RegisterData(){

    }
    // constructor
    public RegisterData(int id, String username,String email_id,String mobile_number){
        this._id = id;
        this.username = username;
        this.email_id=email_id;
        this.mobile_number=mobile_number;
    }


    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    public String getUsername() {
        // TODO Auto-generated method stub
        return username;
    }

    // setting  first name
    public void setUsername(String username){
        this.username =username;
    }
    public String getEmail_id() {
        // TODO Auto-generated method stub
        return email_id;
    }

    public void setEmailId(String email_id){
        this.email_id =email_id;
    }
    public String getMobNo() {
        // TODO Auto-generated method stub
        return mobile_number;
    }

    public void setMobNo(String mobile_number){
        this.mobile_number=mobile_number;
    }
}

