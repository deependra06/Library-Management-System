package utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
  private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm");
  private static final SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("EEEE, MMMM dd, yyyy 'at' hh:mm a");

  public static String formatDate(Date date) {
    return date != null ? DATE_FORMAT.format(date) : "Not set";
  }

  public static String formatDateTime(Date date) {
    return date != null ? DATE_TIME_FORMAT.format(date) : "Not set";
  }

  public static String formatFullDate(Date date) {
    return date != null ? FULL_DATE_FORMAT.format(date) : "Not set";
  }

  public static String formatDueDate(Date date) {
    if (date == null)
      return "Not issued";

    long daysLeft = getDaysUntil(date);
    if (daysLeft < 0) {
      return DATE_FORMAT.format(date) + " (Overdue " + Math.abs(daysLeft) + " days)";
    } else if (daysLeft == 0) {
      return DATE_FORMAT.format(date) + " (Due today)";
    } else {
      return DATE_FORMAT.format(date) + " (" + daysLeft + " days left)";
    }
  }

  public static long getDaysUntil(Date date) {
    if (date == null)
      return -1;
    long diff = date.getTime() - System.currentTimeMillis();
    return TimeUnit.MILLISECONDS.toDays(diff);
  }

  public static String getRelativeTime(Date date) {
    if (date == null)
      return "Never";

    long diff = System.currentTimeMillis() - date.getTime();
    long days = TimeUnit.MILLISECONDS.toDays(diff);

    if (days == 0)
      return "Today";
    if (days == 1)
      return "Yesterday";
    if (days < 7)
      return days + " days ago";
    if (days < 30)
      return (days / 7) + " weeks ago";
    return (days / 30) + " months ago";
  }

  public static Date parseDate(String dateString) {
    try {
      return DATE_FORMAT.parse(dateString);
    } catch (Exception e) {
      try {
        return DATE_TIME_FORMAT.parse(dateString);
      } catch (Exception ex) {
        return null;
      }
    }
  }
}