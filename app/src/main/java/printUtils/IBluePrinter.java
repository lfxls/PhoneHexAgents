package printUtils;

import java.util.List;

public interface IBluePrinter {
	public void printXML(String paramString);
	public void print(String paramString, List paramList);
	public String commitOperation();
}
