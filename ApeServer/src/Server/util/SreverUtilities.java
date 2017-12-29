/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.util;

import LOGIN.UserLogin;
import STUDENT.StuAttendenceCount;
import STUDENT.StuSubjectAttendence;
import STUDENT.StuTimeTable;
import STUDENT.StudentProfile;
import SocketConnect.Response;
import USER.FEEPAY.FeeStructure;
import USER.FEEPAY.PayementDashboard;
import USER.FEEPAY.Payfee;
import USER.LIBRARY.Books;
import USER.LIBRARY.LibraryDashBoard;
import USER.NoticeList;
import USER.Staff;
import Util.RequestedType;
import java.awt.print.Book;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author Shub
 */
public class SreverUtilities {

    static final String url = "jdbc:oracle:oci8:Admin/admin@localhost:1521";

    private static HashSessionMap session  = HashSessionMap.getMap();


    public static Object getDatabaseResponse(RequestedType type, Object ReqObject){
        Response res;
        String q;
        ResultSet rs;
        try {
            OracleDataSource ods = new OracleDataSource();
                    ods.setURL(url);
                    Connection conn = ods.getConnection();
                    Statement  stmt = (java.sql.Statement) conn.createStatement ();
            switch(type){
                case ACADMICCALANDER:
                    break;
                case FACULTYLIST:
                    ArrayList<Staff> staffs = new ArrayList<>();
                    String dept = (String) ReqObject;
                    q = "SELECT S_ID,initcap(S_NAME),upper(DEPARTMENT_ID),S_DESIGNATION,S_EMAIL,NULL,NULL ,NULL FROM staff where department_id like '"
                            + dept +"' order by S_ID ";
                    if(dept == null)
                        q = "SELECT S_ID,initcap(S_NAME),upper(DEPARTMENT_ID),S_DESIGNATION,S_EMAIL,sex,null,null FROM staff  order by S_ID ";
                    rs = stmt.executeQuery(q);
                    while (rs.next()) {
                        staffs.add(new Staff(rs.getString(1), rs.getString(2), rs.getString(5), null, null, null, null, rs.getString(6), rs.getString(4), rs.getString(3), null, null));
                    }
                    res = new Response( null, RequestedType.FACULTYLIST, staffs);
                    return res;
                case LIBRARYINVENTORY:
                    
                    ArrayList<USER.LIBRARY.Books> books  = new ArrayList<>();
                    String title =  (String) ReqObject;
                    q = "select DISTINCT title, authur , publisher ,GENRE , "
                            + "book_count,null,null,null from LIBRARY_BOOKS natural join "
                            + "(select title, count(*) book_count from LIBRARY_BOOKS where "
                            + "LIBRARY_BOOKS.ISSUE_DATE is null GROUP BY LIBRARY_BOOKS.TITLE) where TITLE like '%"
                            + title
                            + "%'";
                    if (title == null)
                        q = "select DISTINCT title, authur , "
                                + "publisher ,GENRE , book_count,null,null,null from "
                                + "LIBRARY_BOOKS natural join (select title, count(*) "
                                + "book_count from LIBRARY_BOOKS WHERE LIBRARY_BOOKS.ISSUE_DATE "
                                + "is null GROUP BY LIBRARY_BOOKS.TITLE )";
                    else if(title.startsWith("stu"))
                        q = "SELECT DISTINCT title, authur , publisher ,GENRE,to_char(ISSUE_DATE,'DD-MM-YYYY'), to_char(ISSUE_DATE+21,'DD-MM-YYYY') "
                                + "due_date, decode(sign((sysdate - ISSUE_DATE - 21) * 5),-1,0,1,round(sysdate -"
                                + " ISSUE_DATE - 21 * 5,2)) late_fee FROM LIBRARY_BOOKS where ISSUE_DATE is not null and ISSUED_TO_S= '"
                                + title
                                + "'";                   
                    else if (title.startsWith("st"))
                            q = "SELECT DISTINCT title, authur , "
                                    + "publisher ,GENRE,null,to_char(ISSUE_DATE,'DD-MM-YY'),to_char(ISSUE_DATE+21,'DD-MM-YYYY') "
                                    + "due_date, decode(sign((sysdate - ISSUE_DATE - 21) * 5),-1,0,1,round(sysdate - ISSUE_DATE - 21 * 5,2)) "
                                    + "late_fee FROM LIBRARY_BOOKS where ISSUE_DATE is not null and ISSUED_TO_STAFF = '"
                                    + title
                                    + "'";
                    rs = stmt.executeQuery(q);
                    while (rs.next()){
                       books.add(new Books(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)));
                    }
                    res = new Response(null,RequestedType.LIBRARYINVENTORY, books);
                    return res;
                    
                case NOTICE:
                    ArrayList<NoticeList> listnotice = new ArrayList<>();
                    q = "select  TO_CHAR(to_date(substr(notice_id,0,2) || '-' ||substr(notice_id,3,2) ||'-'||"
                            + "substr(notice_id,5,2),'YY-MM-DD'),'MON DD') || ',' ||  TO_CHAR( TO_DATE(substr(notice_id,7,2) ||  "
                            + "':' || substr(notice_id,9,2),'HH24:MI'),'HH12:MI PM') DATE_TIME  , TITLE ,DETAILS "
                            + "from NOTICE ORDER BY NOTICE_ID";
                    rs = stmt.executeQuery(q);
                    while (rs.next()) {
                        listnotice.add(new NoticeList(rs.getString(2), rs.getString(3), rs.getString(1)));
                    }
                    res = new Response(null, RequestedType.NOTICE, listnotice);
                    return res;
                case STUTIMETABLE:
                    ArrayList<StuTimeTable.Day> tableDay = new ArrayList<>();
                    ArrayList<StuTimeTable> table = new ArrayList<>();
                    q="select DISTINCT weekday from TIMETABLE";
                    ResultSet rs1 = stmt.executeQuery(q);
                
                    q = "select TIMETABLE.WEEKDAY , to_char(Sysdate,'dd/mm/yyyy'), "
                            + "subject.TITLE , staff.S_NAME,rooms.ROOM_ID from "
                            + "TIMETABLE natural join staff  join rooms on "
                            + "(rooms.ROOM_ID = TIMETABLE.ROOM_ID) join subject on"
                            + "(subject.SUBJECT_ID = TIMETABLE.SUNJECT_ID) order by weekday";
                    
                    rs = stmt.executeQuery(q);
                    
                    for (int j = 0 ; j<1;j++) {                        
                    for(int i = 1 ; i<=5 ; i++) {
                            rs.next();
                            tableDay.add(new StuTimeTable.Day(rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(2)));
                        }
                        table.add(new StuTimeTable(tableDay));
                    }
                    
                    res = new Response(null, RequestedType.STUTIMETABLE, table);
                    return res;
                
                case STUATTENDENCECOUNT:
                    STUDENT.StuAttendenceCount count = new StuAttendenceCount("111", 100, 35);
                    res = new Response(null, RequestedType.STUATTENDENCECOUNT, count);
                    return res;
                case STUDENTPROFILE:
                    STUDENT.StudentProfile stu1 = (StudentProfile) ReqObject;
                    q="select ROLLNO,abs(trunc(MONTHS_BETWEEN(ADDMISSION_DATE,sysdate)/6,0)),CATGRY,DEPARTMENT_ID,EMAIL,PER_ADDRESS,SEX,DOB,G_NAME,ST_NAME from student where rollno = '"
                            + stu1.getRollno()
                            + "'";
                    rs = stmt.executeQuery(q);
                    rs.next();
                    STUDENT.StudentProfile resStu2  = new StudentProfile(stu1.getRollno(), rs.getString(10), rs.getString(9), rs.getString(2), rs.getString(6), rs.getString(6), rs.getString(8), "sdvsv", "45664656", "sdvd", "sdfbsf", "cs");
                    res = new Response(null, RequestedType.GETSTUDETAILS, resStu2);
                    return res;
                case SATFFPROFILE:
                    Staff s111 = (Staff)ReqObject;
                    q = "select S_ID,S_NAME,S_EMAIL,S_ADDRESS,TREAT(S_GRADAUTE as Qualification).quali || ' ' || "
                            + "TREAT(S_GRADAUTE as Qualification).stream ||' ' ||TREAT(S_GRADAUTE as Qualification).compl_year graduate"
                            + " ,TREAT(s_post_graduate as Qualification).quali || ' ' || TREAT(s_post_graduate as Qualification).stream ||' ' "
                            + "||TREAT(s_post_graduate as Qualification).compl_year post_graduate,SEX,S_DESIGNATION,DEPARTMENT_ID,S_MANAGER_ID"
                            + " from staff where S_id = '" +s111.getS_id()+ "'";
                    rs = stmt.executeQuery(q);
                    rs.next();
                    s111 = new Staff(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), null, rs.getString(7), rs.getString(8), rs.getString(9), null, rs.getString(10));
                    res = new Response(null, RequestedType.SATFFPROFILE, s111);
                    return res;
                case STUSUBJECTATTENDENCE:

                    StudentProfile studeent  = (StudentProfile) ReqObject;
                     q  = "select to_number(total) - to_number(attendence) || '/'||"
                             + " total attendence , title FROM(select attendence , total ,"
                             + " s from (select count(*) attendence,LECTURE.SUBJECT_ID s from "
                             + "STU_ATTENDENCE  NATURAL join LECTURE  where STU_ATTENDENCE.STUDENT_ID ="
                             + " '"
                             + studeent.getRollno()
                             + "' group by LECTURE.SUBJECT_ID)natural join("
                             + "select count (*) total,froup.SUBJECT_ID s from LECTURE froup group "
                             + "by subject_id) )d join SUBJECT on (d.s = SUBJECT.SUBJECT_ID)";
                    rs = stmt.executeQuery(q);
                    ArrayList<StuSubjectAttendence.Attendence> liststu = new ArrayList<>();
                    
                    while (rs.next()){
                    liststu.add(new StuSubjectAttendence.Attendence(rs.getString(2), rs.getString(1), null));
                    }
                    res = new Response(null,RequestedType.STUSUBJECTATTENDENCE, liststu);
                    return res;
                case USERLOGIN:
                     UserLogin resUser = (UserLogin) ReqObject;
                     if(session.hasSession(resUser.getUSERNAME())){
                         return new Response("0", RequestedType.USERLOGIN, null);
                     }
                     UserLogin user = new UserLogin(resUser.getUSERNAME(), resUser.getPASSWORD(), 
                             String.valueOf(session.Add(resUser.getUSERNAME())));
                     if(resUser.getUSERNAME().startsWith("stu")){
                        q =  "select * from  student where rollno = '"
                                + resUser.getUSERNAME()
                                + "' and pass = '"
                                + resUser.getPASSWORD()
                                + "'";
                        rs = stmt.executeQuery(q);
                        if(rs.next())
                        res = new Response("1", RequestedType.USERLOGIN, user);
                        else 
                            res = new Response("0", RequestedType.USERLOGIN, resUser);
                        return  res;
                    }
                    else if(resUser.getUSERNAME().startsWith("st")){
                        q =  "select * from STAFF WHERE S_ID = '"
                                + resUser.getUSERNAME()
                                + "' and S_PASSWORD  = '"
                                + resUser.getPASSWORD()
                                + "'";
                        rs = stmt.executeQuery(q);
                        if(rs.next())
                        res = new Response("2", RequestedType.USERLOGIN, user);
                        else 
                            res = new Response("0", RequestedType.USERLOGIN, resUser);
                        return  res;
                    }
                case LIBRARYDASHBOARD:
                    HashMap<String, Integer> i = new HashMap<>();
                    i.put("22-10-2012", 12);
                    i.put("23-10-2012", 20);
                    i.put("24-10-2012", 15);
                    i.put("25-10-2012", 25);
                    i.put("26-10-2012", 5);

                    ArrayList<String> notice = new ArrayList<String>();
                    notice.add("this is the first notice");

                    // LibraryDashBoard dashBoard = new LibraryDashBoard(100, i , 40, "library", null, notice);

                    //  res = new Response("11111", RequestedType.LIBRARYDASHBOARD, dashBoard);
                    //return res;
                case PAYFEE:
                    Payfee p = (Payfee) ReqObject;
                    Payfee pq = new Payfee("", p.getRollno(), null, null, null, null, null, "440");
                    res = new Response("+++", type, pq );
                    return res;
                case GETRECIEPT:
                    Payfee pa = (Payfee) ReqObject;
                    Payfee rePa = new Payfee(null, pa.getRollno(), "dfbfb", pa.getPaidSem(), "27500", "22-02-17 22:22:22", "445450", "44343540");
                    STUDENT.StudentProfile resStu1  = new StudentProfile(null, "Mrityunjay Pandey", "R N Pandey", "2", null, null, "22-12-12 22:22:22", null, "9424397223", null, null, "cs");
                    ArrayList<Object> list = new ArrayList<Object>();
                    list.add(rePa);
                    list.add(resStu1);
                    res = new Response(null, RequestedType.GETRECIEPT, list);
                    return res;
                case GETSTUDETAILS:
                    STUDENT.StudentProfile stu = (StudentProfile) ReqObject;
                    //addmision date = last unpaid semester
                    //DOB = Server DATE TIME
                    STUDENT.StudentProfile resStu  = new StudentProfile(stu.getRollno(), "hcvdv", "vjldvjkb", "2", null, null, "22-12-12 22:22:22", null, "45664656", null, null, "cs");
                    res = new Response(null, RequestedType.GETSTUDETAILS, resStu);
                    return res;
                case PAYMENTDASHBOARD:
                    ArrayList<String> notice1 = new ArrayList<String>();
                    notice1.add("this is the first notice");
                    HashMap<String , String> feepay = new HashMap<>();
                    feepay.put("january","40");
                    feepay.put("february","70");
                    feepay.put("march","100");
                    feepay.put("april","20");

                    ArrayList<FeeStructure> FeeStuct = new ArrayList<>();
                    FeeStuct.add(new FeeStructure("24000", "2000", "10000","20000", "1000"));
                    FeeStuct.add(new FeeStructure("24000", "2000", "10000","20000", "1000"));
                    FeeStuct.add(new FeeStructure("24000", "2000", "10000","20000", "1000"));
                    FeeStuct.add(new FeeStructure("24000", "2000", "10000","20000", "1000"));
                    FeeStuct.add(new FeeStructure("24000", "2000", "10000","20000", "1000"));
                    FeeStuct.add(new FeeStructure("24000", "2000", "10000","20000", "1000"));
                    FeeStuct.add(new FeeStructure("24000", "2000", "10000",null, "1000"));
                    FeeStuct.add(new FeeStructure("24000", "2000", "10000","20000", "1000"));

                    PayementDashboard payD = new PayementDashboard(feepay, FeeStuct, "mritunjay Pandey"
                            ,"2022222", "mritunjayPandey@gamil.com", null, notice1);
                    res = new Response("111", type, payD);
                    return res;

                case ADDEMPLOYEE:
                    Staff s = (Staff) ReqObject;

                    q = "INSERT INTO staff(s_gradaute,s_post_graduate,s_PHD,s_id,s_name,s_email,s_address,sex,s_designation,department_id,s_password,s_manager_id) VALUES (\n" +
                            "                       Qualification("
                            + s.getS_graduate()
                            + "),Qualification("
                            + s.getS_post_graduate()
                            + "),Qualification("
                            + "'BE','coumputer',4"
                            + "),'st'||staff_sequence.nextval,'"
                            + s.getS_name()
                            + "','"
                            + s.getS_email()
                            + "','"
                            + s.getS_Address()
                            + "','"
                            + s.getSex()
                            + "','"
                            + s.getS_designation()
                            + "','"
                            + s.getDepartment_id()
                            + "','"
                            + s.getS_password()
                            + "','"
                            + s.getS_manager_id()
                            +"')";
                    stmt.executeUpdate(q);
                    q = "SELECT s_id FROM staff where S_EMAIL  = '"
                            + s.getS_email()
                            + "'";
                    rs = stmt.executeQuery(q);
                    rs.next();
                    Staff resS = new Staff(rs.getString(1), null,null, null, null, null, null, null, null, null, null, null);
                    res = new Response(null, RequestedType.ADDEMPLOYEE, resS);
                    return res;

                case STUDENTLIST:
                    
                    
                    ArrayList<StudentProfile> students = new ArrayList<>();
                    String dept1 = (String) ReqObject;
                    if(dept1 != null){
                    String[] a = dept1.split(",");
                    q = "select rollno,st_name,g_name,ADDMISSION_DATE,per_address,temp_address,dob,sex,mobile,email,CATGRY,department_id from student where DEPARTMENT_ID = '"
                            + a[0]
                            + "' and abs(trunc(MONTHS_BETWEEN(ADDMISSION_DATE,sysdate)/6)) = "
                            + " " + a[1];}
                    else
                        q = "select rollno,st_name,g_name,ADDMISSION_DATE,per_address,temp_address,dob,sex,mobile,email,CATGRY,department_id from student";
                    rs = stmt.executeQuery(q);
                    while (rs.next()) {
                        students.add(new StudentProfile(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
                    }
                    res = new Response( null, RequestedType.STUDENTLIST, students);
                    return res;
                    
                case ADDSTUDENT:
                    StudentProfile prof = (StudentProfile)ReqObject;
                    q = "Insert into temp_student (fees,st_name,g_name,ADDMISSION_DATE,per_address,temp_address,dob,sex,mobile,email,CATGRY,department_id) VALUES("
                            + "fee_struc('f','f','f','f','f','f','f','f','t'),'"
                            + prof.getSt_name()
                            + "','"
                            +prof.getG_name()
                            + "','"
                            + prof.getAddmission_date()
                            + "','"
                            + prof.getPer_address()
                            + "','"
                            + prof.getTemp_address()
                            + "','"
                            + prof.getDOB()
                            + "','"
                            + prof.getSex()
                            + "',"
                            + prof.getMobile()
                            + ",'"
                            + prof.getEmail()
                            + "','"
                            + prof.getCatgry()
                            + "','"
                            + prof.getDepartment_id()
                            + "') ";
                    stmt.executeUpdate(q);
                    return new Response(null, RequestedType.ADDSTUDENT, null);
                case CLOSEADMISSION:
                    stmt.execute("execute update_student");
                    return new Response(null, RequestedType.CLOSEADMISSION, null);
                case STUDENT_REPORT : 
                    String []sl = ((String) ReqObject).split(",");
                    ArrayList<StuSubjectAttendence.Attendence> att = new ArrayList<>();
                    q = "SELECT STUDENT.ST_NAME , STUDENT.ROLLNO, att || '/'||(select count(lecture_id) from LECTURE where subject_id = '"
                            + sl[0]
                            + "') total  FROM (SELECT COUNT(LECTURE_ID) ATT,STUDENT_ID FROM STU_ATTENDENCE NATURAL JOIN LECTURE WHERE LECTURE.SUBJECT_ID = '"
                            + sl[0]
                            + "' GROUP BY STUDENT_ID) atte JOIN STUDENT on (STUDENT.ROLLNO =atte.STUDENT_ID) WHERE STUDENT.DEPARTMENT_ID = '"
                            + sl[1]
                            + "'";
                    rs = stmt.executeQuery(q);
                    while (rs.next()) {                        
                        att.add(new StuSubjectAttendence.Attendence(rs.getString(1), rs.getString(2),rs.getString(3)));
                    }
                res = new Response(null, RequestedType.STUDENT_REPORT, att);
                return res;
                default:
                    break;
            }

        } catch (SQLException ex) {
            System.out.println(ex);
        }
        res = new Response("aaa", type, ReqObject);
            return  res;
    }
}
