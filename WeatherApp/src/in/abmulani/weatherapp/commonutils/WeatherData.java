package in.abmulani.weatherapp.commonutils;

public class WeatherData {
	private String ID,Main,Desc,Min_Temp,Max_Temp,Pressure,SeaLevel,GroundLevel,Humidity,WindSpeed,WindDegree,TimeStamp,Temperature;

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getMain() {
		return Main;
	}

	public void setMain(String main) {
		Main = main;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getMin_Temp() {
		return (Double.parseDouble(Min_Temp)-273.15)+"";
	}

	public void setMin_Temp(String min_Temp) {
		Min_Temp = min_Temp;
	}

	public String getMax_Temp() {
		return (Double.parseDouble(Max_Temp)-273.15)+"";
	}

	public void setMax_Temp(String max_Temp) {
		Max_Temp = max_Temp;
	}

	public String getPressure() {
		return Pressure;
	}

	public void setPressure(String pressure) {
		Pressure = pressure;
	}

	public String getSeaLevel() {
		return SeaLevel;
	}

	public void setSeaLevel(String seaLevel) {
		SeaLevel = seaLevel;
	}

	public String getGroundLevel() {
		return GroundLevel;
	}

	public void setGroundLevel(String groundLevel) {
		GroundLevel = groundLevel;
	}

	public String getHumidity() {
		return Humidity;
	}

	public void setHumidity(String humidity) {
		Humidity = humidity;
	}

	public String getWindSpeed() {
		return WindSpeed;
	}

	public void setWindSpeed(String windSpeed) {
		WindSpeed = windSpeed;
	}

	public String getWindDegree() {
		return WindDegree;
	}

	public void setWindDegree(String windDegree) {
		WindDegree = windDegree;
	}

	public String getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		TimeStamp = timeStamp;
	}

	public String getTemperature() {
		return Temperature;
	}

	public void setTemperature(String temperature) {
		Temperature = temperature;
	}
}
