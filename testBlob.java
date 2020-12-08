public class testBlob{
    public static void main(String args[]) throws Exception {
        Blob myBlob = new Blob("hello, world!");
        myBlob.getValue(myBlob.getKey());

        Blob mySecondBlob = new Blob("Test 2");
        mySecondBlob.getValue(mySecondBlob.getKey());

        Blob myThirdBlob = new Blob("Test 3");
        myThirdBlob.getValue(myThirdBlob.getKey());
    }
}
