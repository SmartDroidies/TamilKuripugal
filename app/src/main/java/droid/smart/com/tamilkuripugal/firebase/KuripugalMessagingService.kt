package droid.smart.com.tamilkuripugal.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

/***
 * Follow https://firebase.google.com/docs/cloud-messaging/android/client for implementation
 * dy1AbMcpHE8:APA91bG-lQispY6HTLMfLieosL5mdz6H_V6Yl3tRyxfRJ5C8yBjE2uwXDNWTZGLdiD5CtoPDCbuLQdaa4hR8D7i9-A4PBrULuut-welAWJYO-ES2p1Nd_m6gXMwQyJIzmHHBH_yPG7oa
 */
class KuripugalMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        Timber.d("Refreshed token: $p0")
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Timber.d("Message data payload: %s ", remoteMessage.data)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Timber.d("Message Notification Body: ${it.body}")
        }
    }
}


/*
api_key=AIzaSyCTddNtg_zJE_U0dJlhUfsd0hqwYmzW-Zg
curl -X  POST -H "Authorization: key=$api_key" -H Content-Type:"application/json" -d '{"registration_ids":["f2ZPmNkohW8:APA91bGt2tablyIi2arHuuXUuENzxyIMhYsEYN1GGbjhteK5SyrtNWl5BahlRD00oAFTAHdezh-XFygZVs_JTImevaQ8hYFV6-Zpvai5wNUWfIj0Q-Cl5nDWYmGqzN50JcfZZM0Av9i7"],"notification":{"body":"\u0b9a\u0bc8\u0ba9\u0bb8\u0bcd \u0b8e\u0ba9\u0bcd\u0bb1\u0bbe\u0bb2\u0bcd \u0b8e\u0ba9\u0bcd\u0ba9?","title":"Tamil Kuripugal"},"data":{"id":17330}}' https://fcm.googleapis.com/fcm/send
*/

/*
# api_key=AIzaSyCTddNtg_zJE_U0dJlhUfsd0hqwYmzW-Zg
curl -X  POST -H "Authorization: key=$api_key" -H Content-Type:"application/json" -d '{"to":"/topics/native-health","notification":{ "title": "Tamil Kuripugal", "body": "Valuable Tip"}, "data": {"id":17330}}' https://fcm.googleapis.com/fcm/send
*/

