package com.sparta.unit;

import com.sparta.utils.JsonDataReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JsonDataReaderTest {

    private JsonDataReader dataReader;

    @BeforeEach
    void setUp() {
        dataReader = new JsonDataReader();
    }

    @Test
    @DisplayName("Should successfully read an existing JSON file from resources")
    void testReadExistingFile() {
        String content = dataReader.readJsonFile("testdata/spartans.json");
        assertNotNull(content);
        assertTrue(content.contains("Jonathan"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file does not exist")
    void testReadNonExistentFile() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dataReader.readJsonFile("testdata/non_existent.json")
        );
        assertTrue(exception.getMessage().contains("File not found in resources"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when path is null")
    void testReadNullPath() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dataReader.readJsonFile(null)
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when path is empty string")
    void testReadEmptyPath() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dataReader.readJsonFile("   ")
        );
    }
}