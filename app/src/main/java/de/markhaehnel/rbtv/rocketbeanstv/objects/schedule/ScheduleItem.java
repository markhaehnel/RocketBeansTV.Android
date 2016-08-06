
package de.markhaehnel.rbtv.rocketbeanstv.objects.schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import de.markhaehnel.rbtv.rocketbeanstv.utils.Time;
import lombok.Getter;

@Getter
public class ScheduleItem {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("topic")
    @Expose
    private String topic;
    @SerializedName("show")
    @Expose
    private String show;
    @SerializedName("timeStart")
    @Expose
    private String timeStart;
    @SerializedName("timeEnd")
    @Expose
    private String timeEnd;
    @SerializedName("length")
    @Expose
    private Long length;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("game")
    @Expose
    private String game;

    public String getTimeStartShort() {
        return Time.getShortTimeFromISO(getTimeStart());
    }
}
