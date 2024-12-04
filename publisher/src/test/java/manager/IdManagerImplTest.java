package manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for the IdManagerImpl class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(IdManagerImpl.class)
public class IdManagerImplTest {

    private IdManagerImpl idManager;

    /**
     * Sets up the test environment before each test.
     */
    @Before
    public void setUp() {
        idManager = new IdManagerImpl();
    }

    /**
     * Tests the readIdFile method to ensure it reads the file correctly and populates the ids list.
     */
    @Test
    public void testReadIdFile() throws Exception {
        BufferedReader readerMock = mock(BufferedReader.class);
        when(readerMock.readLine()).thenReturn("id1", "id2", "id3", null);

        FileReader fileReaderMock = mock(FileReader.class);
        PowerMockito.whenNew(FileReader.class).withArguments("testfile.txt").thenReturn(fileReaderMock);
        PowerMockito.whenNew(BufferedReader.class).withArguments(fileReaderMock).thenReturn(readerMock);

        idManager.readIdFile("testfile.txt");

        assertEquals(3, idManager.getFileSize());
        List<String> ids = idManager.ids;
        assertEquals("id1", ids.get(0));
        assertEquals("id2", ids.get(1));
        assertEquals("id3", ids.get(2));
    }

    /**
     * Tests the getFileSize method to ensure it returns the correct size of the ids list.
     */
    @Test
    public void testGetFileSize() {
        idManager.ids.add("id1");
        idManager.ids.add("id2");
        assertEquals(2, idManager.getFileSize());
    }

    /**
     * Tests the getBatch method to ensure it returns the correct batch of ids.
     */
    @Test
    public void testGetBatch() {
        idManager.ids.add("id1");
        idManager.ids.add("id2");
        idManager.ids.add("id3");
        idManager.divideInto(2);

        String[] batch1 = idManager.getBatch();
        assertArrayEquals(new String[]{"id1", "id2"}, batch1);

        String[] batch2 = idManager.getBatch();
        assertArrayEquals(new String[]{"id3"}, batch2);

        String[] batch3 = idManager.getBatch();
        assertNull(batch3);
    }

    /**
     * Tests the divideInto method to ensure it divides the ids list into the correct number of batches.
     */
    @Test
    public void testDivideInto() {
        idManager.ids.add("id1");
        idManager.ids.add("id2");
        idManager.ids.add("id3");
        idManager.divideInto(2);

        assertEquals(2, idManager.batchSize);
        assertEquals(0, idManager.currentBatch);
    }
}