package typeCheck;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class main {
	static BufferedReader br = null;

	public static void main(String[] args) {
		try {
			br = new BufferedReader(new FileReader("./test.txt"));
			typecheck mycheck = new typecheck();
			// as long as there is a nextline we continue to read
			String input;
			while ((input = br.readLine()) != null) {
				if (input.isEmpty()) {
					continue;// shouldn't need this
				}
				mycheck.check(input);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
