package com.example.policyengine;

import com.example.policyengine.facts.SampleFact;
import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PolicyengineApplicationTests {

    @Test
    public void contextLoads() {

    }

    @Ignore
    @Test
    public void convertStringToObject() {

        try {        
            String messageAsString = "[{\"ObjectType\":\"SampleFact\"},{\"value\":\"SampleFactValue\"}]" ;
            JSONArray messageAsJSONOArray = new JSONArray(messageAsString);
            String objectType = messageAsJSONOArray.getJSONObject(0).getString("ObjectType");
            
            String objectAsString = messageAsJSONOArray.get(1).toString();
            
            if (objectType.equalsIgnoreCase("SampleFact")) {
                SampleFact sampleFact = new Gson().fromJson(objectAsString, SampleFact.class);
                System.out.println("convertStringToObject result for sample fact object " + sampleFact.getValue());
            }
        } catch (JSONException ex) {
            Logger.getLogger(PolicyengineApplicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
