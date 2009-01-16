String[] args = this.interpreter.get("bsh.args");
if (args == null || args.length != 2) {
	System.err.println("The source and destination files must be specified");
	System.exit(-1);
}

import java.io.*;

InputStream is = new FileInputStream(args[0]);
OutputStream os = new FileOutputStream(args[1]);
Reader in = new InputStreamReader(is, "UTF-8");
Writer out = new OutputStreamWriter(os, "UTF-8");

char[] buf = new char[1024];
int len = in.read(buf, 0, buf.length - 1); //keep one space
for (int j = 0; j < len; ++j) {
	if (Character.isWhitespace(buf[j]))
		continue;

	if (buf[j] == '/' && buf[++j] == '*') {
		for (;;) {
			if (++j >= len) {
				out.write(buf, 0, len);
				break;
			}
			if (buf[j] == '*' && buf[++j] == '/') {
				if (++j < len)
					out.write(buf, j, len - j);
				break;
			}
		}
		break;
	}
	out.write(buf, 0, len);
	break;
}

while ((len = in.read(buf, 0, buf.length)) >= 0)
	out.write(buf, 0, len);

out.close();
os.close();
in.close();
is.close();
