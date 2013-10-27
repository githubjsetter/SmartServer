package com.inca.npbi.server;

/**
 * 时间维度参数
 * 
 * @author user
 * 
 */
public class Timeparaminfo {
	private String timetype = "";
	private String year = "";
	private String month = "";
	private String day = "";
	private String year1 = "";
	private String month1 = "";
	private String day1 = "";

	private String startdate = "";
	private String enddate = "";
	private String npbi_instanceid = "";

	public String getTimetype() {
		return timetype;
	}

	public void setTimetype(String timetype) {
		if (timetype == null)
			timetype = "";
		this.timetype = timetype;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getNpbi_instanceid() {
		return npbi_instanceid;
	}

	public void setNpbi_instanceid(String npbi_instanceid) {
		this.npbi_instanceid = npbi_instanceid;
	}

	public void setYear(String year) {
		if (year == null)
			year = "";
		this.year = year;
	}

	public void setMonth(String month) {
		if (month == null)
			month = "";
		this.month = month;
	}

	public void setDay(String day) {
		if (day == null)
			day = "";
		this.day = day;
	}

	public void setYear1(String year1) {
		if (year1 == null)
			year1 = "";
		this.year1 = year1;
	}

	public void setMonth1(String month1) {
		if (month1 == null)
			month1 = "";
		this.month1 = month1;
	}

	public void setDay1(String day1) {
		if (day1 == null)
			day1 = "";
		this.day1 = day1;
	}

	public String getYear() {
		return fillZero(year, 4);
	}

	public String getMonth() {
		return fillZero(month, 2);
	}

	public String getDay() {
		return fillZero(day, 2);
	}

	public String getYear1() {
		return fillZero(year1, 4);
	}

	public String getMonth1() {
		return fillZero(month1, 2);
	}

	public String getDay1() {
		return fillZero(day1, 2);
	}

	/**
	 * 取上月的年
	 * 
	 * @return
	 */
	public String getPriormonthYear() {
		int im = 0;
		int iy = 0;
		iy = Integer.parseInt(getYear());
		im = Integer.parseInt(getMonth());
		if (im == 1) {
			return fillZero(String.valueOf(iy - 1), 4);
		}
		return getYear();
	}

	// 取上月的月
	public String getPriormonthMonth() {
		int im = 0;
		im = Integer.parseInt(getMonth());
		if (im == 1) {
			return "12";
		}
		return fillZero(String.valueOf(im - 1), 2);
	}

	String fillZero(String s, int l) {
		if (s == null || s.length() == 0)
			return s;
		while (s.length() < l) {
			s = "0" + s;
		}
		return s;
	}
	
	public void genNpbi_instanceid(){
		year=fillZero(year,4);
		month=fillZero(month,2);
		day=fillZero(day,2);
		year1=fillZero(year1,4);
		month1=fillZero(month1,2);
		day1=fillZero(day1,2);

		if(timetype.equals("year")){
			npbi_instanceid=year;
			if(year1.length()>0){
				npbi_instanceid+="-"+year1;
			}
		}else if(timetype.equals("month")){
			npbi_instanceid=year+month;
			if(year1.length()>0 && month1.length()>0){
				npbi_instanceid+="-"+year1+month1;
			}
			
		}else if(timetype.equals("day")){
			npbi_instanceid=year+month+day;
			if(year1.length()>0 && month1.length()>0 && day1.length()>0){
				npbi_instanceid+="-"+year1+month1+day1;
			}
		}
		

	}
}
