package droid.smart.com.tamilkuripugal.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

/***
 * Follow https://firebase.google.com/docs/cloud-messaging/android/client for implementation
 * fl2Zkv0E7kw:APA91bFHURRFKJ7MEri8Ub9F39LBzcm54WHCSO59DnooTbP3hS-LziFN5qneTQqcaLMgQzznE1-Afrov14hNSXlTBjiEPUl5Wt_ax2a9_IHxkVff0-J4CmhSj2zy67_6eBFxzRT1EGsg
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
api_key=AIzaSyAt-REJGpyVckE5CziobNeJ_f33OHrUDT4
curl -X  POST -H "Authorization: key=$api_key" -H Content-Type:"application/json" -d '{"registration_ids":["fl2Zkv0E7kw:APA91bFHURRFKJ7MEri8Ub9F39LBzcm54WHCSO59DnooTbP3hS-LziFN5qneTQqcaLMgQzznE1-Afrov14hNSXlTBjiEPUl5Wt_ax2a9_IHxkVff0-J4CmhSj2zy67_6eBFxzRT1EGsg"],"notification":{"body":"\u0b9a\u0bc8\u0ba9\u0bb8\u0bcd \u0b8e\u0ba9\u0bcd\u0bb1\u0bbe\u0bb2\u0bcd \u0b8e\u0ba9\u0bcd\u0ba9?","title":"Tamil Kuripugal"},"data":{"id":17330}}' https://fcm.googleapis.com/fcm/send
*/

/*
# api_key=AIzaSyAt-REJGpyVckE5CziobNeJ_f33OHrUDT4
curl -X  POST -H "Authorization: key=$api_key" -H Content-Type:"application/json" -d '{"to":"/topics/native-health","notification":{ "title": "Tamil Kuripugal", "body": "Valuable Tip"}, "data": {"id":17330}}' https://fcm.googleapis.com/fcm/send
*/

