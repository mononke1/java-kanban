package taskmanagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testTasksEqualityById() {
        Epic epic1 = new Epic("epicTest1", "testTasksEqualityById1", 3);
        Epic epic2 = new Epic("epicTest1", "testTasksEqualityById1", 3);
        assertEquals(epic1, epic2, "задачи не равны");

        Epic epic3 = new Epic("epicTest1", "testTasksEqualityById1", 3);
        Epic epic4 = new Epic("epicTest1", "testTasksEqualityById1", 4);
        assertNotEquals(epic3, epic4);
    }
}