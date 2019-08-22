import org.junit.Test;

public class TestFullCommands {
    @Test
    public void testFolderSimple() {
        ImageTester.main("-f src/test/TestData -debug".split(" "));
    }

    @Test
    public void testPDFSimple() {
        ImageTester.main("-f src/test/TestData/b/c/JustPDF/Lorem2.pdf -debug".split(" "));
    }

    @Test
    public void testPDFSplitted(){
        ImageTester.main("-f src/test/TestData/b/c/JustPDF/Lorem3.pdf -th 10 -debug -st".split(" "));
    }
}
