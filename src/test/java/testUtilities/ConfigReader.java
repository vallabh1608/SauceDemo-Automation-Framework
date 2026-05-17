package testUtilities;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader
{
	private Properties props;
	public ConfigReader(String filePath) {
		props = new Properties();
		try {
			FileInputStream fis = new FileInputStream(filePath);
			props.load(fis);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getString(String key) {
		return props.getProperty(key);
	}
	public int getInt(String key) {
		return Integer.parseInt(props.getProperty(key));
	}
}