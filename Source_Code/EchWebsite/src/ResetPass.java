

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mypkg.Connect;
import mypkg.CreatePassword;
import mypkg.Password;

/**
 * Servlet implementation class ResetPass
 */
@WebServlet("/ResetPass")
public class ResetPass extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ResetPass() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			int valid=0;
			String np="",ipass="";
			String newpass="",name="";
			String email=request.getParameter("email").toString().trim();
			String q;
			ResultSet rt,rts;

			Connection con=Connect.database();
			Statement st=con.createStatement();

			q="SELECT COUNT(EMAIL) AS COUNT FROM Anurag.STUDENT WHERE STUDENT.EMAIL='"+email+"'";
			rt=st.executeQuery(q);
			rt.next();
			if(rt.getInt("COUNT")==1)
			{
				np="SELECT NAME,PASSWORD FROM Anurag.STUDENT WHERE STUDENT.EMAIL='"+email+"'";
				q="UPDATE Anurag.STUDENT ";
				valid=1;
			}
			else
			{
				q="SELECT COUNT(EMAIL) AS COUNT FROM Anurag.PROF_ADMIN WHERE PROF_ADMIN.EMAIL='"+email+"'";
				rts=st.executeQuery(q);
				rts.next();
				if(rts.getInt("COUNT")==1)

				{
					np="SELECT NAME,PASSWORD FROM Anurag.PROF_ADMIN WHERE EMAIL='"+email+"'";
					q="UPDATE Anurag.PROF_ADMIN ";
					valid=1;
				}

			}

			if(valid==1){
				Statement stm=con.createStatement();
				ResultSet rst=stm.executeQuery(np);
				rst.next();
				ipass=rst.getString("PASSWORD").substring(0,6).toLowerCase()+
						rst.getString("PASSWORD").substring(8,15).toUpperCase();
				ipass=CreatePassword.random(10,ipass);
				newpass=Password.encrypt(ipass);
				name=rst.getString("NAME").trim();
                q+="SET PASSWORD='"+newpass+"' WHERE email like '%"+email+"%'";
				st.executeUpdate(q);
				response.getWriter().print("Password Changed Succesfully");

				request.setAttribute("to",new String(email));
				request.setAttribute("sub",new String("Success : Password Reset"));
				request.setAttribute("msg",new String("Hello "+name+", your request to reset your password for Echelon Institute of Technology is complete."+
						"You may now login with the following details : \n Username :"+email+"\nPassword: "+ipass 
						+"\nThis is a autogenerated password & you may change it for further use.") );
				request.setAttribute("reqFrom",new String("ResetPass"));
				
				RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/email.jsp");
				rd.forward(request, response);
			}
			else
			{response.getWriter().print("Invalid Email : Try Again");}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			response.getWriter().print("Error Occured : Try Again");
			e.printStackTrace();
		}
	}

}
