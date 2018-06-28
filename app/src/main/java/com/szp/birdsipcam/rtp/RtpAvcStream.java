package com.szp.birdsipcam.rtp;


class RtpAvcStream extends RtpStream {

    //private final static String TAG = "AvcRtpStream";

    RtpAvcStream(RtpSocket socket) {
        super(96, 90000, socket);
    }

//    public void addNalu(ByteBuffer buf, int size, long timeUs) throws IOException {
//        byte[] data = new byte[size];
//        buf.get(data);
//
//        addNalu(data, size, timeUs);
//    }

//    private void addNalu(byte[] data, int size, long timeUs) throws IOException {
//        if (size <= 1400) {
//            createSingleUnit(data, 0, size, timeUs);
//        } else {
//            createFuA(data, 0, size, timeUs);
//        }
//    }

//    private void createSingleUnit(byte[] data, int offset, int size, long timeUs) throws IOException {
//
//        //Log.i(TAG, "single nalu  type:" +  (data[offset] & 0x1f));
//
//        addPacket(data, offset, size, timeUs);
//    }


//    private void createFuA(byte[] data, int offset, int size, long timeUs) throws IOException {
//
//        byte originHeader = data[offset++];
//        size -= 1;
//
//        //Log.i(TAG, "FuA nalu  type:" +  (originHeader & 0x1f));
//
//
//        int left = size;
//        int read = 1400;
//
//
//        for (; left > 0; left -= read, offset += read) {
//            byte indicator = (byte) ((originHeader & 0xe0) | 28);
//            byte naluHeader = (byte) (originHeader & 0x1f);
//
//            if (left < read) {
//                read = left;
//            }
//
//            if (left == size)
//                naluHeader = (byte) (naluHeader | (1 << 7));
//            else if (left == read)
//                naluHeader = (byte) (naluHeader | (1 << 6));
//
//
//            addPacket(new byte[]{indicator, naluHeader}, data, offset, read, timeUs);
//        }
//
//    }
}