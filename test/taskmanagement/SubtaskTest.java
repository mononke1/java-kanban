package taskmanagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testSubtaskEqualityById() {
        Subtask subtask1 = new Subtask("taskTest1", "testTasksEqualityById1", TaskStatus.NEW, 0 ,1);
        Subtask subtask2 = new Subtask("taskTest1", "testTasksEqualityById1", TaskStatus.NEW, 0 ,1);
        assertEquals(subtask1, subtask2, "задачи не равны");

        Subtask subtask3 = new Subtask("taskTest1", "testTasksEqualityById1", TaskStatus.NEW, 0 ,1);
        Subtask subtask4 = new Subtask("taskTest1", "testTasksEqualityById1", TaskStatus.NEW, 0 ,2);
        assertNotEquals(subtask3, subtask4);
    }
}