package droid.smart.com.tamilkuripugal.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

/***
 * Follow https://firebase.google.com/docs/cloud-messaging/android/client for implementation
 * dy1AbMcpHE8:APA91bG-lQispY6HTLMfLieosL5mdz6H_V6Yl3tRyxfRJ5C8yBjE2uwXDNWTZGLdiD5CtoPDCbuLQdaa4hR8D7i9-A4PBrULuut-welAWJYO-ES2p1Nd_m6gXMwQyJIzmHHBH_yPG7oa
 */
class KuripugalMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String?) {
        Timber.d("Refreshed token: $p0")
    }
}


/*
# api_key=YOUR_SERVER_KEY
AIzaSyCTddNtg_zJE_U0dJlhUfsd0hqwYmzW-Zg
curl -X  POST -H "Authorization: key=$api_key" -H Content-Type:"application/json" -d '{"notification":{"title":"FCM Message","body":"This is a Firebase Cloud Messaging Topic Message"},{"registration_ids":["dy1AbMcpHE8:APA91bG-lQispY6HTLMfLieosL5mdz6H_V6Yl3tRyxfRJ5C8yBjE2uwXDNWTZGLdiD5CtoPDCbuLQdaa4hR8D7i9-A4PBrULuut-welAWJYO-ES2p1Nd_m6gXMwQyJIzmHHBH_yPG7oa"]}}' https://fcm.googleapis.com/fcm/send
*/