package nlp.robot;

//import java.util.Scanner;


public class RobotReply {
	public static String Reply(String question) {
		SmartRobot SR = new SmartRobot();
		String reply=null;
		//取消警告
	//	@SuppressWarnings("resource")
		if(question!=null)
			reply=SR.getMessage(question);
		return reply;
	}
}
