package BaseCalc;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

public class BaseCalcSpeechlet implements SpeechletV2 {



    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> speechletRequestEnvelope) {

    }

    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> speechletRequestEnvelope) {

        String speechText =  "Welcome to base converter, please specify the number and bases you wish to convert";

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech,reprompt);
    }

    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> speechletRequestEnvelope) {
        IntentRequest intentRequest = speechletRequestEnvelope.getRequest();
        Intent intent = intentRequest.getIntent();
        String intentName = intent.getName();

        try {
            if(intentName.equals("CalcIntent")) {
                String number = intent.getSlot("number").getValue().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                String baseOfNumber = intent.getSlot("baseOfNumber").getValue();
                String baseTo = intent.getSlot("baseTo").getValue().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

                int baseOfNumberI = (baseOfNumber != null) ? Integer.parseInt(parseBase(baseOfNumber.replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) : 10;

                Long num = baseToBase(number,
                        baseOfNumberI,
                        Integer.parseInt(parseBase(baseTo))
                );

                return getCalcResponse(num,number,baseTo);
            }
            if(intentName.equals("AMAZON.CancelIntent")){
                String speechText = "Canceling";

                SimpleCard card = new SimpleCard();
                card.setTitle("Base Conversion");
                card.setContent(speechText);

                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText(speechText);

                return SpeechletResponse.newTellResponse(speech,card);
            }
            else{
                String speechText =  "Sorry I didn't catch that, please try again or say cancel to finish";

                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText(speechText);

                Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(speech);

                return SpeechletResponse.newAskResponse(speech,reprompt);
            }
        }catch (NumberFormatException e){
            String speechText =  "Sorry I could not calculate that, " +
                        "please say a valid number and bases you wish to convert";

            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);

            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(speech);

            return SpeechletResponse.newAskResponse(speech,reprompt);
        }catch (NullPointerException e){
            String speechText =  "Sorry I could not calculate that, " +
                    "please say a valid number and bases you wish to convert";

            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);

            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(speech);

            return SpeechletResponse.newAskResponse(speech,reprompt);
        }


    }

    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> speechletRequestEnvelope) {

    }

    private String parseBase(String base){
        base = base.replaceAll(" ","");
        if(base.equals("hex") || base.equals("hexa") || base.equals("hexadecimal"))
            return "16";
        if(base.equals("octa") || base.equals("octal") || base.equals("octalic"))
            return "8";
        if(base.equals("dec") || base.equals("decimal") )
            return "10";
        if(base.equals("binary"))
            return "2";
        else
            return base;
    }



    public SpeechletResponse getCalcResponse(long number,String initialNumber, String baseTarget){
        String separatedNumber = "";
        if(baseTarget.equals("binary") || baseTarget.equals("2")) {
            Stack<Long> stack = new Stack<Long>();
            while (number != 0) {
                stack.add(number % 10);
                number /= 10;
            }
            while (!stack.empty()) {
                separatedNumber += stack.pop() + " ";
            }
        }else
            separatedNumber = ""+number;
        String speechText = "the number " + initialNumber +" in base "+baseTarget +" is " + separatedNumber;

        SimpleCard card = new SimpleCard();
        card.setTitle("Base Conversion");
        card.setContent(speechText);

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech,card);
    }
    public static long baseToBase(String num,int baseFrom,int baseTo){
        Hashtable<String,Integer> vals = new Hashtable<String,Integer>();
        vals.put("0",0); vals.put("1",1); vals.put("2",2); vals.put("3",3); vals.put("4",4); vals.put("5",5);
        vals.put("6",6); vals.put("7",7); vals.put("8",8); vals.put("9",9); vals.put("a",10); vals.put("b",11);
        vals.put("c",12); vals.put("d",13); vals.put("e",14); vals.put("f",15); vals.put("g",16); vals.put("h",17);
        vals.put("i",18); vals.put("j",19); vals.put("k",20); vals.put("l",21); vals.put("m",22); vals.put("n",23);
        vals.put("o",24); vals.put("p",25);vals.put("q",26); vals.put("r",27);vals.put("s",28); vals.put("t",29);
        vals.put("u",30); vals.put("v",31);vals.put("w",32); vals.put("x",33);vals.put("y",34); vals.put("z",35);

        long numberInDesc = 0;
        long numberInNewBase = 0;
        long power = 1;
        String number;

        byte [] strAsByteArray = num.getBytes();
        byte [] result = new byte [strAsByteArray.length];

        for (int i = 0; i<strAsByteArray.length; i++)
            result[i] = strAsByteArray[strAsByteArray.length-i-1];

        number = new String(result);

        for(Character c: number.toCharArray()){
            numberInDesc += power * vals.get(""+c);
            power *= baseFrom;
        }
        power = 1;
        while(numberInDesc > 0){
            numberInNewBase += power * (numberInDesc % baseTo);
            numberInDesc /= baseTo;
            power *= 10;

        }
        return numberInNewBase;
    }

}
