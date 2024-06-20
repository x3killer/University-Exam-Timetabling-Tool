package readInput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DayDate {

	static final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

	//returns the previous date of the given date
	public static String getPreviousDate(String  curDate) {
		Date date;

		try {
			date = df.parse(curDate);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			return df.format(calendar.getTime());
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	//returns the next date of the given date
	public static String getNextDate(String  curDate) {
		Date date;

		try {
			date = df.parse(curDate);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			return df.format(calendar.getTime());
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	//returns the day of the given date.
	public static String getDayName(String  curDate) {

		try {
			Date date = df.parse(curDate);
			date = df.parse(curDate);
			SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
			return dayFormat.format(date);

		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
}
