package balsoftware.rruleparser.control;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jfxtras.labs.icalendarfx.properties.component.recurrence.RecurrenceRule;
import jfxtras.labs.icalendarfx.properties.component.time.DateTimeStart;

/**
 * Control for the recurrence rule parser
 * Requires iCalendarFX
 * See RFC 5545 iCalendar 3.3.10 for details
 * 
 * @author David Bal
 *
 */
public class RRuleParserVBox extends VBox
{
    @FXML private TextField rruleTextField;
    @FXML private Label rruleLabel;
    @FXML private TextField dtstartTextField;
    @FXML private Label dtstartLabel;
    @FXML private TextField recurrenceTextField;
    @FXML private Label recurrenceLabel;
    @FXML private TextArea recurrenceSetTextArea;
    
    final private String originalRecurrenceLabel;
    final private String originalRRuleLabel;
    final private String originalDTStartLabel;
    
    private ObjectProperty<Integer> recurrenceMaxProperty = new SimpleObjectProperty<>();
    private ObjectProperty<RecurrenceRule> rruleProperty = new SimpleObjectProperty<>();
    private ObjectProperty<DateTimeStart> dtstartProperty = new SimpleObjectProperty<>();
    
    public RRuleParserVBox( )
    {
        super();
        loadFxml(RRuleParserVBox.class.getResource("RRuleParser.fxml"), this);
        originalRecurrenceLabel = recurrenceLabel.getText();
        originalRRuleLabel = rruleLabel.getText();
        originalDTStartLabel = dtstartLabel.getText();

        // Defaults
        Integer max = (recurrenceTextField.getText().isEmpty()) ? null : Integer.parseInt(recurrenceTextField.getText());
        recurrenceMaxProperty.set(max);
        RecurrenceRule rrule = (rruleTextField.getText().isEmpty()) ? null : RecurrenceRule.parse(rruleTextField.getText());
        rruleProperty.set(rrule);
        dtstartTextField.setText(new DateTimeStart(LocalDate.now()).toContent());
    }
    
    @FXML public void initialize()
    {
        recurrenceMaxProperty.addListener(obs -> calcRecurrences());
        rruleProperty.addListener(obs -> calcRecurrences());
        dtstartProperty.addListener(obs -> calcRecurrences());
        
        recurrenceTextField.textProperty().addListener((obs, oldValue, newValue) ->
        {
            boolean isNumber = recurrenceTextField.getText().matches("[0-9]+");
            final Integer max;
            if (isNumber)
            {
                recurrenceLabel.setText(originalRecurrenceLabel);
                recurrenceLabel.setStyle("-fx-text-fill: black");
                max = Integer.parseInt(newValue);
            } else if (newValue.isEmpty())
            {
                recurrenceLabel.setText(originalRecurrenceLabel);
                recurrenceLabel.setStyle("-fx-text-fill: black");
                max = null;
            } else
            {
                recurrenceLabel.setText(originalRecurrenceLabel + " INVALID");
                recurrenceLabel.setStyle("-fx-text-fill: red");
                max = null;
            }
            recurrenceMaxProperty.set(max);
        });
        
        rruleTextField.textProperty().addListener((obs, oldValue, newValue) ->
        {
            try
            {
                if (! newValue.isEmpty())
                {
                    rruleProperty.set(RecurrenceRule.parse(newValue));
                }
                rruleLabel.setText(originalRRuleLabel);
                rruleLabel.setStyle("-fx-text-fill: black");
            } catch (Exception e)
            {
                rruleProperty.set(null);
                rruleLabel.setText(originalRRuleLabel + " INVALID");
                rruleLabel.setStyle("-fx-text-fill: red");
            }
        });
        
        dtstartTextField.textProperty().addListener((obs, oldValue, newValue) ->
        {
            try
            {
                if (! newValue.isEmpty())
                {
                    dtstartProperty.set(DateTimeStart.parse(newValue));
                }
                dtstartLabel.setText(originalDTStartLabel);
                dtstartLabel.setStyle("-fx-text-fill: black");
            } catch (Exception e)
            {
                dtstartProperty.set(null);
                dtstartLabel.setText(originalDTStartLabel + " INVALID");
                dtstartLabel.setStyle("-fx-text-fill: red");
            }
        });
    }

    private void calcRecurrences()
    {
        final String text;
        if (recurrenceMaxProperty.get() != null && rruleProperty.get() != null && dtstartProperty.get() != null)
        {
            text = rruleProperty.get().getValue().streamRecurrences(dtstartProperty.get().getValue())
                    .limit(recurrenceMaxProperty.get())
                    .map(t -> t.toString())
                    .collect(Collectors.joining(System.lineSeparator()));
        } else
        {
            text = null;
        }
        recurrenceSetTextArea.setText(text);
    }
        
    protected static void loadFxml(URL fxmlFile, Object rootController)
    {
        FXMLLoader loader = new FXMLLoader(fxmlFile);
        loader.setController(rootController);
        loader.setRoot(rootController);
        try {
            loader.load();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
