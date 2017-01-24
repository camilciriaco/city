package app.appurservice.sigin;

/**
 * Created by RVN-TECH on 8/10/2016.
 */
public class FormDetailData {
    //private variables
    int _id;
    String first_name;
    String last_name;
    String middle_i;
    String email_id;
    String mobile_number;
    String landline;
    String bday_Month;
    int bday_day;
    int bday_year;
    String address;
    String gender;
    String civilstatus;

    /*public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    String password;
*/
    // Empty constructor
    public FormDetailData(){

    }
    // constructor
    public FormDetailData(int id, String first_name, String  last_name,String email_id,String landline, String mobile_number, String bday_Month,
                          int bday_day, int bday_year, String civilstatus, String address, String gender, String middle_i){
        this._id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email_id=email_id;
        this.mobile_number=mobile_number;
        this.bday_year= bday_year;
        this.bday_day = bday_day;
        this.bday_Month = bday_Month;
        this.landline = landline;
        this.address = address;
        this.gender= gender;
        this.middle_i = middle_i;
        this.civilstatus = civilstatus;


    }


    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    public String getfirstName() {
        // TODO Auto-generated method stub
        return first_name;
    }

    // setting  first name
    public void setfirstName(String first_name){
        this.first_name =first_name;
    }
    public String getlastName() {
        // TODO Auto-generated method stub
        return last_name;
    }

    public void setlastName(String last_name){
        this.last_name =last_name;
    }

    public String getMiddle_i() {
        // TODO Auto-generated method stub
        return first_name;
    }

    // setting  middle name
    public void setMiddle_i(String middle_i){
        this.middle_i =middle_i;
    }

    public String getEmailId() {
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

    public void setLandline(String landline){
        this.landline=landline;
    }

    public String getLandline() {
        // TODO Auto-generated method stub
       return landline;
    }

    public void setAddress(String address){
        this.address=address;
    }

    public String getAddress() {
        // TODO Auto-generated method stub
        return address;
    }

    public void setBday_day(int bday_day){
        this.bday_day=bday_day;
    }

    public int getBday_day() {
        // TODO Auto-generated method stub
        return bday_day;
    }

    public void setBday_year(int bday_year){
        this.bday_year=bday_year;
    }

    public int getBday_year() {
        // TODO Auto-generated method stub
        return bday_year;
    }
    public void setCivilstatus(String civilstatus){
        this.civilstatus=civilstatus;
    }

    public String getCivilstatus() {
        // TODO Auto-generated method stub
        return civilstatus;
    }
    public void setGender(String gender){
        this.gender=gender;
    }

    public String getGender() {
        // TODO Auto-generated method stub
        return gender;
    }
    public void setBday_Month(String bday_Month){
        this.bday_Month=bday_Month;
    }

    public String getBday_Month() {
        // TODO Auto-generated method stub
        return bday_Month;
    }


}
