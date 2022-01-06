package com.emc.sett.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader implements Closeable {

	protected int skipLines;
	protected BufferedReader br;
	protected boolean hasNext = true;
	protected boolean linesSkiped;
	protected boolean keepCR;
	protected boolean verifyReader;
	protected long linesRead = 0L;
	protected long recordsRead = 0L;

	public CSVReader(Reader reader) {
		this.br = ((reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader));

		this.skipLines = 0;
		this.keepCR = false;
		this.verifyReader = true;
	}

	public int getSkipLines() {
		return this.skipLines;
	}

	public boolean keepCarriageReturns() {
		return this.keepCR;
	}

	public List<String[]> readAll() throws IOException {
		List<String[]> allElements = new ArrayList<String[]>();
		while (this.hasNext) {
			String[] nextLineAsTokens = readNext();
			if (nextLineAsTokens != null) {
				allElements.add(nextLineAsTokens);
			}
		}
		return allElements;
	}

	public String[] readNext() throws IOException {
		String[] result = null;
		String nextLine = getNextLine();
		if (!this.hasNext) {
			return validateResult(result);
		}
		result = nextLine.split(",", -1);
		return validateResult(result);
	}

	protected String[] validateResult(String[] result) {
		if (result != null) {
			this.recordsRead += 1L;
		}
		return result;
	}

	protected String getNextLine() throws IOException {
		if (isClosed()) {
			this.hasNext = false;
			return null;
		}

		if (!this.linesSkiped) {
			for (int i = 0; i < this.skipLines; i++) {
				this.br.readLine();
				this.linesRead += 1L;
			}
			this.linesSkiped = true;
		}
		String nextLine = this.br.readLine();
		if (nextLine == null)
			this.hasNext = false;
		else {
			this.linesRead += 1L;
		}

		return this.hasNext ? nextLine : null;
	}

	protected boolean isClosed() {
		if (!this.verifyReader)
			return false;
		try {
			this.br.mark(2);
			int nextByte = this.br.read();
			this.br.reset();
			return nextByte == -1;
		} catch (IOException e) {
		}
		return true;
	}

	public void close() throws IOException {
		this.br.close();
	}

	public boolean verifyReader() {
		return this.verifyReader;
	}

	public long getLinesRead() {
		return this.linesRead;
	}

	public long getRecordsRead() {
		return this.recordsRead;
	}
}