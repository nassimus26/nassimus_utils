package org.file;
import java.io.*;
import java.nio.channels.Channels;

/*
* Nassim MOUALEK
* cd_boite@yahoo.fr
* */
public class FastScanner implements Closeable {
    private static int MARK_MAX_DEFAULT = 128;
    private InputStream inputStream;
    private RandomAccessFile randomAccessFile;
    private static int buffer_size = 2*1024;
    private byte[] buffer;
    private byte[] lineDelimeter = "\n".getBytes();
    private CurrentByteListener currentByteListener;
    private int readed;
    private int offset;
    private int maxRead;

    public void setLineDelimeter(byte[] lineDelimeter) {
        this.lineDelimeter = lineDelimeter;
    }
    public FastScanner(InputStream inputStream) {
        this(inputStream, buffer_size);
    }
    public FastScanner(RandomAccessFile randomAccessFile) {
        this( Channels.newInputStream((randomAccessFile.getChannel()) ), buffer_size );
    }
    public FastScanner(RandomAccessFile randomAccessFile, int buffer_size) {
        this( Channels.newInputStream((randomAccessFile.getChannel()) ), buffer_size );
        this.randomAccessFile = randomAccessFile;
    }
    public FastScanner(InputStream inputStream, int buffer_size) {
        this.inputStream = inputStream;
        buffer = new byte[buffer_size];
        if (MARK_MAX_DEFAULT<buffer_size)
            MARK_MAX_DEFAULT = buffer_size/2;
        this.maxRead = buffer.length;
    }

    public FastScanner(byte[] buffer) {
        this.buffer = buffer;
        if (MARK_MAX_DEFAULT<buffer.length)
            MARK_MAX_DEFAULT = buffer.length/2;
        this.maxRead = buffer.length;
    }
    public final byte[] nextLine(boolean clean) throws IOException {
        byte[] row = this.readToElement(lineDelimeter, MoveEnum.RIGHT_FROM_ELEMENT);
        if (clean) {
            return cleanRow(row);
        }else
            return row;
    }
    public static final byte[] cleanRow(byte[] row) throws IOException {
        try {
            int remove = 0;
            if (row[row.length - 1] == '\n')
                remove = 1;
            if (row[row.length - 2] == '\r')
                remove = 2;
            byte[] result = new byte[row.length - remove];
            System.arraycopy(row, 0, result, 0, row.length - remove);
            return result;
        }catch (RuntimeException e){
            throw e;
        }
    }
    public final byte[] nextLine(byte[] delimiter) throws IOException {
        return this.readToElement(delimiter, MoveEnum.RIGHT_FROM_ELEMENT);
    }

    public final byte[] readToElement(String element, MoveEnum moveEnum) throws IOException {
        return readToElement(element.getBytes(), moveEnum);
    }
    public final byte[] readToElement(byte[] elementIntArray, MoveEnum moveEnum) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(30);
        if (MoveEnum.LEFT_FROM_ELEMENT==moveEnum)
            readToLeftElement(elementIntArray, outputStream);
        else
            readToRightElement(elementIntArray, outputStream);
        outputStream.close();
        return outputStream.toByteArray();
    }

    public final String readToElementStr(String element, MoveEnum moveEnum) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (MoveEnum.LEFT_FROM_ELEMENT==moveEnum)
            readToLeftElement(element, outputStream);
        else
            readToRightElement(element, outputStream);
        outputStream.close();
        return outputStream.toString();
    }

    private int bufferOffset;
    private int markSize;
    private int markPosition;
    private void mark(int markSize) {
        this.markSize = markSize;
        this.markPosition = bufferOffset;
    }
    private final void reset() {
        offset = offset + (bufferOffset-markPosition);
        bufferOffset = markPosition;
        markSize = 0;
        markPosition = 0;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) throws Exception {
        if (this.randomAccessFile==null)
            throw new Exception("setOffset supported only with randomAccessFile");
        bufferOffset = bufferOffset-(this.offset-offset);

        if (bufferOffset<0 || bufferOffset>=maxRead || this.offset==0) {
            bufferOffset = 0;
            randomAccessFile.getChannel().position(offset);
            readed = 0;
        }

        this.offset = offset;
        markSize = 0;
        markPosition = 0;
    }

    private boolean isStreamTerminated = false;
    public boolean isStreamTerminated() {
        return isStreamTerminated;
    }

    public void setCurrentByteListener(CurrentByteListener currentByteListener) {
        this.currentByteListener = currentByteListener;
    }

    private final byte read() throws IOException {
        byte b = read_();
        if (currentByteListener!=null)
            currentByteListener.handle(b);
        return b;
    }
    public void setMaxRead(int maxRead) throws Exception {
        if (maxRead>buffer.length)
            maxRead = buffer.length;
        this.maxRead = maxRead;
        if (bufferOffset >= maxRead)
            setOffset(offset-bufferOffset);
    }
    private final byte read_() throws IOException{

        if (isStreamTerminated)
            return -1;

        if ( bufferOffset >= maxRead || readed==0 ){
            offset = offset + markSize;
            bufferOffset = markSize;

            if (markSize>0){
                for (int i=0;i<markSize;i++)
                    buffer[i] = buffer[maxRead-markSize+i];
                markPosition = markSize -(maxRead - markPosition);
            }

            readed = -1;
            if (inputStream!=null)
                readed = inputStream.read( buffer, bufferOffset,maxRead - bufferOffset);

            if ( readed == -1 ){
                isStreamTerminated = true;
                return -1;
            }
        }

        if( readed<buffer.length && bufferOffset >=readed ) {
            isStreamTerminated = true;
            return -1;
        }
        byte result = buffer[bufferOffset];
        offset++;
        bufferOffset++;

        return result;
    }

    public final void readToRightElement(String element, OutputStream outputStream) throws IOException {
        byte[] elementIntArray = element.getBytes();
        readToRightElement(elementIntArray, outputStream);
    }

    public final void readToRightElement(byte[] elementIntArray, OutputStream outputStream) throws IOException {
        int elementSize = elementIntArray.length;
        int index = 0;
        byte[] tmp = new byte[elementSize];
        int c;
        while ((c = read()) != -1) {
            if (c == elementIntArray[index]) {
                tmp[index++] = (byte) c;
            } else {
                if (index>0)
                    outputStream.write(tmp, 0, index);
                index = 0;
                outputStream.write(c);
            }
            if (index == elementSize) {
                outputStream.write(tmp);
                break;
            }
        }
    }
    public final void readToLeftElement(String element, OutputStream outputStream) throws IOException {
        byte[] elementIntArray = element.getBytes();
        readToLeftElement(elementIntArray, outputStream);
    }
    public final void readToLeftElement(byte[] elementIntArray, OutputStream outputStream) throws IOException {
        int elementSize = elementIntArray.length;
        int index = 0;
        byte[] tmp = new byte[elementSize];
        int c;
        mark(elementSize);
        while ((c = read()) != -1) {
            if (c == elementIntArray[index]) {
                tmp[index++] = (byte) c;
            } else {
                if (index>0)
                    outputStream.write(tmp, 0, index);
                mark(elementSize);
                index = 0;
                outputStream.write(c);
            }
            if (index == elementSize) {
                reset();
                break;
            }
        }
    }
    public final boolean moveToNextLeftElement(String element) throws IOException {
        byte[] elementIntArray = element.getBytes();
        return moveToNextLeftElement(elementIntArray);
    }
    public final boolean moveToNextLeftElement(byte[] elementIntArray) throws IOException {
        int index = 0;
        int c;
        int elementSize = elementIntArray.length;
        mark(elementSize);
        while ((c = read()) != -1) {
            if (c == (int) elementIntArray[index]) {
                index++;
            } else {
                mark(elementSize);
                index = 0;
            }
            if (index == elementSize) {
                reset();
                return true;
            }
        }
        return false;
    }
    public final boolean moveToNextRightElement(String element) throws IOException {
        byte[] elementIntArray = element.getBytes();
        return moveToNextRightElement(elementIntArray);
    }
    public final boolean moveToNextRightElement(byte[] elementIntArray) throws IOException {
        int index = 0;
        int c;
        int elementSize = elementIntArray.length;
        while ((c = read()) != -1) {
            if (c == (int) elementIntArray[index]) {
                index++;
            } else {
                index = 0;
            }
            if (index == elementSize) {
                return true;
            }
        }
        return false;
    }
    public final boolean moveToNextElement(String element, MoveEnum moveEnum) throws IOException {
        return moveToNextElement(element.getBytes(), moveEnum);
    }
    public final boolean moveToNextElement(byte[] elements, MoveEnum moveEnum) throws IOException {
        if (MoveEnum.LEFT_FROM_ELEMENT == moveEnum)
            return moveToNextLeftElement(elements);
        else
            return moveToNextRightElement(elements);
    }
    public final byte[] retrieveNextXmlTagBytes(MoveEnum moveEnum) throws IOException {
        return retrieveNextTokenBytes('<', '>', moveEnum, MARK_MAX_DEFAULT);
    }
    public final byte[] retrieveNextXmlTagBytes(MoveEnum moveEnum, FastByteArrayOutputStream outputStream) throws IOException {
        return retrieveNextTokenBytes('<', '>', moveEnum, MARK_MAX_DEFAULT, outputStream);
    }
    public final byte[] retrieveNextXmlTagRightBytes() throws IOException {
        return retrieveNextRightTokenBytes('<', '>');
    }
    public final byte[] retrieveNextXmlTagRightBytes(FastByteArrayOutputStream outputStream) throws IOException {
        return retrieveNextRightTokenBytes('<', '>', outputStream);
    }
    public final byte[] retrieveNextXmlTagLeftBytes() throws IOException {
        return retrieveNextLeftTokenBytes('<', '>', MARK_MAX_DEFAULT);
    }
    public final String retrieveNextXmlTag(MoveEnum moveEnum) throws IOException {
        return retrieveNextToken('<', '>', moveEnum, MARK_MAX_DEFAULT);
    }

    public final String retrieveNextToken(String leftAsArray, String rightAsArray, MoveEnum moveEnum) throws IOException {
        return retrieveNextToken(leftAsArray, rightAsArray, moveEnum, MARK_MAX_DEFAULT);
    }

    public final String retrieveNextToken(String elementLeft, String elementRight, MoveEnum moveEnum, int markMax) throws IOException {
        byte[] retrieveNextTokenBytes;
        if (moveEnum==MoveEnum.LEFT_FROM_ELEMENT)
            retrieveNextTokenBytes = retrieveNextTokenLeftBytes(elementLeft.getBytes(), elementRight.getBytes(), markMax);
        else
            retrieveNextTokenBytes = retrieveNextTokenRightBytes(elementLeft.getBytes(), elementRight.getBytes());
        if (retrieveNextTokenBytes == null) {
            return null;
        }
        return new String(retrieveNextTokenBytes);
    }

    public final String retrieveNextToken(char elementLeft, char elementRight, MoveEnum moveEnum, int markMax) throws IOException {
        byte[] retrieveNextTokenBytes = retrieveNextTokenBytes(elementLeft, elementRight, moveEnum, markMax);
        if (retrieveNextTokenBytes == null) {
            return null;
        }
        return new String(retrieveNextTokenBytes);
    }
    public final byte[] retrieveNextTokenBytes(char elementLeft, char elementRight, MoveEnum moveEnum, int markMax) throws IOException {
        return retrieveNextTokenBytes(elementLeft, elementRight, moveEnum, markMax, new FastByteArrayOutputStream());
    }
    public final byte[] retrieveNextTokenBytes(char elementLeft, char elementRight, MoveEnum moveEnum, int markMax, FastByteArrayOutputStream outputStream) throws IOException {
        if (MoveEnum.LEFT_FROM_ELEMENT==moveEnum)
            return retrieveNextLeftTokenBytes(elementLeft, elementRight, markMax, outputStream);
        else
            return retrieveNextRightTokenBytes(elementLeft, elementRight, outputStream);
    }
    public final byte[] retrieveNextRightTokenBytes(char elementLeft, char elementRight) throws IOException {
        return retrieveNextRightTokenBytes(elementLeft, elementRight, new FastByteArrayOutputStream());
    }
    public final byte[] retrieveNextRightTokenBytes(char elementLeft, char elementRight, FastByteArrayOutputStream outputStream) throws IOException {
        int c;
        outputStream.reset();
        boolean isElementLeftFounds = false;
        while ((c = read()) != -1) {
            if (c == elementLeft) {
                isElementLeftFounds = true;
                break;
            }
        }

        if (!isElementLeftFounds) {
            return null;
        }
        outputStream.write(elementLeft);

        while ((c = read()) != -1) {
            outputStream.write(c);
            if (c == elementRight) {
                return outputStream.toByteArray();
            }
        }
        return null;
    }
    public final byte[] retrieveNextLeftTokenBytes(char elementLeft, char elementRight, int markMax) throws IOException {
        return retrieveNextLeftTokenBytes(elementLeft, elementRight, markMax, new FastByteArrayOutputStream());
    }
    public final byte[] retrieveNextLeftTokenBytes(char elementLeft, char elementRight, int markMax, FastByteArrayOutputStream outputStream) throws IOException {
        int c;
        outputStream.reset();
        mark(markMax);
        boolean isElementLeftFounds = false;
        while ((c = read()) != -1) {
            if (c == elementLeft) {
                isElementLeftFounds = true;
                break;
            }
            mark(markMax);
        }
        if (!isElementLeftFounds) {
            reset();
            return null;
        }
        outputStream.write(elementLeft);

        while ((c = read()) != -1) {
            outputStream.write(c);
            if (c == elementRight) {
                reset();
                return outputStream.toByteArray();
            }
        }
        reset();
        return null;
    }
    public final byte[] retrieveNextTokenRightBytes(byte[] elementLeft, byte[] elementRight) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        retrieveNextTokenRightBytes(elementLeft, elementRight, outputStream);
        if (outputStream.size()==0)
            return null;
        return outputStream.toByteArray();
    }
    public final void retrieveNextTokenRightBytes(byte[] elementLeft, byte[] elementRight, OutputStream outputStream) throws IOException {
        int c;
        boolean isElementLeftFounds = false;
        int leftIndex = 0;
        while ((c = read()) != -1) {
            if (c == elementLeft[leftIndex]) {
                leftIndex++;
            } else {
                leftIndex = 0;
                continue;
            }
            if (leftIndex == elementLeft.length) {
                isElementLeftFounds = true;
                break;
            }
        }

        if (!isElementLeftFounds) {
            return;
        }
        outputStream.write(elementLeft);

        int rightIndex = 0;
        while ((c = inputStream.read()) != -1) {
            outputStream.write(c);
            if (c == elementRight[rightIndex]) {
                rightIndex++;

            } else {
                rightIndex = 0;
                continue;
            }
            if (rightIndex == elementRight.length) {
                break;
            }
        }
    }

    public final byte[] retrieveNextTokenLeftBytes(byte[] elementLeft, byte[] elementRight, int markMax) throws IOException {
        int c;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mark(markMax);

        boolean isElementLeftFounds = false;
        int leftIndex = 0;
        while ((c = read()) != -1) {
            if (c == elementLeft[leftIndex]) {
                leftIndex++;
            } else {
                mark(markMax);
                leftIndex = 0;
                continue;
            }
            if (leftIndex == elementLeft.length) {
                isElementLeftFounds = true;
                break;
            }
        }

        if (!isElementLeftFounds) {
            reset();
            return null;
        }
        outputStream.write(elementLeft);

        int rightIndex = 0;
        while ((c = inputStream.read()) != -1) {
            outputStream.write(c);
            if (c == elementRight[rightIndex]) {
                rightIndex++;

            } else {
                rightIndex = 0;
                continue;
            }
            if (rightIndex == elementRight.length) {
                reset();
                return outputStream.toByteArray();
            }
        }
        reset();
        return null;
    }
    @Override
    public final void close() throws IOException {
        inputStream.close();
    }

}