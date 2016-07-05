package balsoftware.rruleparser;
	
import balsoftware.rruleparser.control.RRuleParserVBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Recurrence Rule Parser
 * See RFC 5545 iCalendar 3.3.10 for details
 * 
 * @author David Bal
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new RRuleParserVBox();
			Scene scene = new Scene(root,500,495);
			primaryStage.setScene(scene);
			primaryStage.setTitle("RRULE Parser");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
