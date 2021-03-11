package wang.jvaeyhcd.flutter_baidu_face_plugin;

/**
 * Author: 苗
 * Time: $(Date)
 * Description:
 */
public class MessageEvent {
    public final String message;
    public final String type;

    public static MessageEvent getInstance(String type,String message) {
        return new MessageEvent(type,message);
    }

    private MessageEvent(String type,String message) {
        this.message = message;
        this.type = type;
    }
}
