package uk.co.glamoor.gateway.config;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import javax.annotation.PostConstruct;

@Configuration
public class AuthConfig {
    
    @PostConstruct
    public void initFirebase() throws Exception {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json")))
                .build();
        FirebaseApp.initializeApp(options);


        /*
         * For AWS deploy
         */
        // FirebaseOptions options = FirebaseOptions.builder()
        //         .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(
        //                 System.getenv("FIREBASE_SERVICE_ACCOUNT_KEY").getBytes())))
        //         .build();
    }
}
