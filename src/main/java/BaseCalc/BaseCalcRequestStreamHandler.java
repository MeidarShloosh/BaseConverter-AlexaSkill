package BaseCalc;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;

public class BaseCalcRequestStreamHandler  extends SpeechletRequestStreamHandler{
    private static final HashSet<String> supportedApplicationIds = new HashSet<String>();
    static{
        supportedApplicationIds.add("amzn1.ask.skill.de169580-0125-45bc-920c-d106f727005b");
    }
    public BaseCalcRequestStreamHandler(){
        super(new BaseCalcSpeechlet(),supportedApplicationIds);
    }



}
