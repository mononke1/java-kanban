package taskmanagement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testTasksEqualityById() {
        Task task1 = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        Task task2 = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        assertEquals(task1, task2, "задачи не равны");

        Task task3 = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 1);
        Task task4 = new Task("taskTest", "testTasksEqualityById", TaskStatus.NEW, 2);
        assertNotEquals(task3, task4);
    }
}
