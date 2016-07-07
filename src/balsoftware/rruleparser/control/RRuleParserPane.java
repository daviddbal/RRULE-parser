package balsoftware.rruleparser.control;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import jfxtras.labs.icalendarfx.properties.component.recurrence.RecurrenceRule;
import jfxtras.labs.icalendarfx.properties.component.time.DateTimeStart;

/**
 * Control for the recurrence rule parser
 * Requires iCalendarFX
 * See RFC 5545 iCalendar 3.3.10 for details
 * 
 * Copyright (c) 2016, David Bal
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author David Bal
 */
public class RRuleParserPane extends AnchorPane
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
    
    final private static int MAX_ALLOWED = 100_000;
    
    @FXML private Hyperlink myHyperlink;
    public Hyperlink getMyHyperlink() { return myHyperlink; }
    
    public RRuleParserPane( )
    {
        super();
        loadFxml(RRuleParserPane.class.getResource("RRuleParser.fxml"), this);
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
        // invalidation listeners to update dates when any change occurs
        recurrenceMaxProperty.addListener(obs -> calcRecurrences());
        rruleProperty.addListener(obs -> calcRecurrences());
        dtstartProperty.addListener(obs -> calcRecurrences());
        
        // listeners to check for valid values
        recurrenceTextField.textProperty().addListener((obs, oldValue, newValue) ->
        {
            boolean isNumber = recurrenceTextField.getText().matches("[0-9]+");
            final int max = isNumber ? Integer.parseInt(newValue) : 0;
            boolean isTooBig = max > MAX_ALLOWED;
            if (isNumber && ! isTooBig)
            {
                recurrenceLabel.setText(originalRecurrenceLabel);
                recurrenceLabel.setStyle("-fx-text-fill: black");
            } else if (newValue.isEmpty())
            {
                recurrenceLabel.setText(originalRecurrenceLabel);
                recurrenceLabel.setStyle("-fx-text-fill: black");
            } else
            {
                recurrenceLabel.setText(originalRecurrenceLabel + " INVALID");
                recurrenceLabel.setStyle("-fx-text-fill: red");
            }
            
            if (! isTooBig)
            {
                recurrenceMaxProperty.set(max);                
            } else
            {
                recurrenceMaxProperty.set(null);
            }
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
        
        String text = "iCalendar RRule Parser Information" + System.lineSeparator()
                + System.lineSeparator() +
                "This software parses an iCalendar RRULE string into a series of date/times." + System.lineSeparator() +
                "It uses iCalendarFX, an iCalendar API utilizing JavaFX and Java 8," + System.lineSeparator() +
                "also written by David Bal" + System.lineSeparator() +
                "See RFC 5545 iCalendar 3.3.10 for details" + System.lineSeparator()
                + System.lineSeparator() + 
                "Please direct any questions or comments to David Bal at dbal@balsoftware.org" + System.lineSeparator()
                + System.lineSeparator() + 
                " * Copyright (c) 2016, David Bal" + System.lineSeparator() + 
                " * All rights reserved." + System.lineSeparator() + 
                " * " + System.lineSeparator() + 
                " * Redistribution and use in source and binary forms, with or without" + System.lineSeparator() + 
                " * modification, are permitted provided that the following conditions are met:" + System.lineSeparator() + 
                " *     * Redistributions of source code must retain the above copyright" + System.lineSeparator() + 
                " *       notice, this list of conditions and the following disclaimer." + System.lineSeparator() + 
                " *     * Redistributions in binary form must reproduce the above copyright" + System.lineSeparator() + 
                " *       notice, this list of conditions and the following disclaimer in the" + System.lineSeparator() + 
                " *       documentation and/or other materials provided with the distribution." + System.lineSeparator() + 
                " *     * Neither the name of the organization nor the" + System.lineSeparator() + 
                " *       names of its contributors may be used to endorse or promote products" + System.lineSeparator() + 
                " *       derived from this software without specific prior written permission." + System.lineSeparator() + 
                " * " + System.lineSeparator() + 
                " * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND" + System.lineSeparator() + 
                " * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED" + System.lineSeparator() + 
                " * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE" + System.lineSeparator() + 
                " * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY" + System.lineSeparator() + 
                " * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES" + System.lineSeparator() + 
                " * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;" + System.lineSeparator() + 
                " * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND" + System.lineSeparator() + 
                " * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT" + System.lineSeparator() + 
                " * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS" + System.lineSeparator() + 
                " * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
        Tooltip.install(myHyperlink, new Tooltip(text));
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
    
    @FXML void handleHyperlink()
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("RRule Parser Software Information");
        alert.setHeaderText(
                "Parses a iCalendar RRULE string into a series of date/times." + System.lineSeparator() +
                "This software uses iCalendarFX, an iCalendar API utilizing JavaFX and Java 8," + System.lineSeparator() +
                "also written by David Bal" + System.lineSeparator() +
                "See RFC 5545 iCalendar 3.3.10 for details" + System.lineSeparator() + System.lineSeparator() + 
                "Please direct any questions or comments to David Bal at dbal@balsoftware.org");
        alert.setContentText(" * Copyright (c) 2016, David Bal" + System.lineSeparator() + 
" * All rights reserved." + System.lineSeparator() + 
" * " + System.lineSeparator() + 
" * Redistribution and use in source and binary forms, with or without" + System.lineSeparator() + 
" * modification, are permitted provided that the following conditions are met:" + System.lineSeparator() + 
" *     * Redistributions of source code must retain the above copyright" + System.lineSeparator() + 
" *       notice, this list of conditions and the following disclaimer." + System.lineSeparator() + 
" *     * Redistributions in binary form must reproduce the above copyright" + System.lineSeparator() + 
" *       notice, this list of conditions and the following disclaimer in the" + System.lineSeparator() + 
" *       documentation and/or other materials provided with the distribution." + System.lineSeparator() + 
" *     * Neither the name of the organization nor the" + System.lineSeparator() + 
" *       names of its contributors may be used to endorse or promote products" + System.lineSeparator() + 
" *       derived from this software without specific prior written permission." + System.lineSeparator() + 
" * " + System.lineSeparator() + 
" * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND" + System.lineSeparator() + 
" * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED" + System.lineSeparator() + 
" * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE" + System.lineSeparator() + 
" * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY" + System.lineSeparator() + 
" * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES" + System.lineSeparator() + 
" * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;" + System.lineSeparator() + 
" * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND" + System.lineSeparator() + 
" * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT" + System.lineSeparator() + 
" * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS" + System.lineSeparator() + 
" * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.");
        alert.showAndWait();
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
