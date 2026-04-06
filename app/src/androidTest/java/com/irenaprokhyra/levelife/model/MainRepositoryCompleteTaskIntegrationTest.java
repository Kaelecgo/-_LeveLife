package com.irenaprokhyra.levelife.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(AndroidJUnit4.class)
public class MainRepositoryCompleteTaskIntegrationTest {

    private AppDatabase db;
    private UserDao userDao;
    private TaskDao taskDao;
    private TaskCompletionDao taskCompletionDao;
    private MainRepository repository;

    private final AtomicLong fixedNow = new AtomicLong();
    private TimeZone originalTimeZone;

    @Before
    public void setUp() {
        originalTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        Context context = ApplicationProvider.getApplicationContext();

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        userDao = db.userDao();
        taskDao = db.taskDao();
        taskCompletionDao = db.taskCompletionDao();

        Executor directExecutor = Runnable::run;
        repository = new MainRepository(db, directExecutor, fixedNow::get);
    }

    @After
    public void tearDown() {
        if (db != null) {
            db.close();
        }
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    public void completeTask_onceTask_firstCompletion_rewardsAndMarksCompleted() {
        fixedNow.set(calendarOf(2026, Calendar.APRIL, 4, 10, 20, 0, 0).getTimeInMillis());

        User user = new User("irena", "hashed");
        long userId = userDao.insertUser(user);

        Task task = new Task(
                (int) userId,
                "Salir",
                "Descripcion",
                Task.CATEGORY_GENERAL,
                20,
                10,
                0,
                Task.DIFFICULTY_MEDIUM,
                Task.FREQUENCY_ONCE,
                false
        );
        long taskId = taskDao.insertTask(task);

        TestTaskCompleteCallback callback = new TestTaskCompleteCallback();

        repository.completeTask((int) taskId, (int) userId, callback);

        User updatedUser = userDao.getUserById((int) userId);
        Task updatedTask = taskDao.getTaskById((int) taskId);

        assertTrue(callback.successCalled);
        assertFalse(callback.errorCalled);
        assertEquals(20, callback.rewardXP);
        assertEquals(10, callback.rewardBerries);
        assertEquals(0, callback.ecoReward);

        assertEquals(1, updatedUser.getLevel());
        assertEquals(20, updatedUser.getExperience());
        assertEquals(10, updatedUser.getBerries());
        assertEquals(0, updatedUser.getEcoCoins());

        assertTrue(updatedTask.isCompleted());
        assertEquals(fixedNow.get(), updatedTask.getLastCompletedAt());

        assertEquals(1, taskCompletionDao.countCompletionsForTask((int) taskId));
    }

    @Test
    public void completeTask_onceTask_secondCompletion_returnsErrorAndDoesNotChangeState() {
        fixedNow.set(calendarOf(2026, Calendar.APRIL, 4, 10, 20, 0, 0).getTimeInMillis());

        User user = new User("irena", "hashed");
        long userId = userDao.insertUser(user);

        Task task = new Task(
                (int) userId,
                "Salir",
                "Descripcion",
                Task.CATEGORY_GENERAL,
                20,
                10,
                0,
                Task.DIFFICULTY_MEDIUM,
                Task.FREQUENCY_ONCE,
                false
        );
        long taskId = taskDao.insertTask(task);

        TestTaskCompleteCallback firstCallback = new TestTaskCompleteCallback();
        repository.completeTask((int) taskId, (int) userId, firstCallback);

        User userAfterFirst = userDao.getUserById((int) userId);
        Task taskAfterFirst = taskDao.getTaskById((int) taskId);
        int historyAfterFirst = taskCompletionDao.countCompletionsForTask((int) taskId);

        TestTaskCompleteCallback secondCallback = new TestTaskCompleteCallback();
        repository.completeTask((int) taskId, (int) userId, secondCallback);

        User userAfterSecond = userDao.getUserById((int) userId);
        Task taskAfterSecond = taskDao.getTaskById((int) taskId);
        int historyAfterSecond = taskCompletionDao.countCompletionsForTask((int) taskId);

        assertFalse(secondCallback.successCalled);
        assertTrue(secondCallback.errorCalled);
        assertNotNull(secondCallback.errorMessage);

        assertEquals(userAfterFirst.getExperience(), userAfterSecond.getExperience());
        assertEquals(userAfterFirst.getBerries(), userAfterSecond.getBerries());
        assertEquals(userAfterFirst.getEcoCoins(), userAfterSecond.getEcoCoins());

        assertEquals(taskAfterFirst.isCompleted(), taskAfterSecond.isCompleted());
        assertEquals(taskAfterFirst.getLastCompletedAt(), taskAfterSecond.getLastCompletedAt());

        assertEquals(historyAfterFirst, historyAfterSecond);
        assertEquals(1, historyAfterSecond);
    }

    @Test
    public void completeTask_recurringTask_samePeriod_secondCompletionReturnsError() {
        fixedNow.set(calendarOf(2026, Calendar.APRIL, 4, 10, 20, 0, 0).getTimeInMillis());

        User user = new User("irena", "hashed");
        long userId = userDao.insertUser(user);

        Task task = new Task(
                (int) userId,
                "Beber agua",
                "Descripcion",
                Task.CATEGORY_HEALTH,
                10,
                5,
                0,
                Task.DIFFICULTY_EASY,
                Task.FREQUENCY_DAILY,
                false
        );
        long taskId = taskDao.insertTask(task);

        TestTaskCompleteCallback firstCallback = new TestTaskCompleteCallback();
        repository.completeTask((int) taskId, (int) userId, firstCallback);

        User userAfterFirst = userDao.getUserById((int) userId);
        Task taskAfterFirst = taskDao.getTaskById((int) taskId);
        int historyAfterFirst = taskCompletionDao.countCompletionsForTask((int) taskId);

        TestTaskCompleteCallback secondCallback = new TestTaskCompleteCallback();
        repository.completeTask((int) taskId, (int) userId, secondCallback);

        User userAfterSecond = userDao.getUserById((int) userId);
        Task taskAfterSecond = taskDao.getTaskById((int) taskId);
        int historyAfterSecond = taskCompletionDao.countCompletionsForTask((int) taskId);

        assertTrue(firstCallback.successCalled);
        assertFalse(secondCallback.successCalled);
        assertTrue(secondCallback.errorCalled);

        assertFalse(taskAfterFirst.isCompleted());
        assertFalse(taskAfterSecond.isCompleted());

        assertEquals(userAfterFirst.getExperience(), userAfterSecond.getExperience());
        assertEquals(userAfterFirst.getBerries(), userAfterSecond.getBerries());
        assertEquals(userAfterFirst.getEcoCoins(), userAfterSecond.getEcoCoins());

        assertEquals(historyAfterFirst, historyAfterSecond);
        assertEquals(1, historyAfterSecond);
    }

    @Test
    public void completeTask_recurringTask_nextPeriod_allowsCompletionAgain() {
        fixedNow.set(calendarOf(2026, Calendar.APRIL, 4, 10, 20, 0, 0).getTimeInMillis());

        User user = new User("irena", "hashed");
        long userId = userDao.insertUser(user);

        Task task = new Task(
                (int) userId,
                "Beber agua",
                "Descripcion",
                Task.CATEGORY_HEALTH,
                10,
                5,
                0,
                Task.DIFFICULTY_EASY,
                Task.FREQUENCY_DAILY,
                false
        );
        long taskId = taskDao.insertTask(task);

        TestTaskCompleteCallback firstCallback = new TestTaskCompleteCallback();
        repository.completeTask((int) taskId, (int) userId, firstCallback);

        User userAfterFirst = userDao.getUserById((int) userId);
        Task taskAfterFirst = taskDao.getTaskById((int) taskId);
        int historyAfterFirst = taskCompletionDao.countCompletionsForTask((int) taskId);

        fixedNow.set(calendarOf(2026, Calendar.APRIL, 5, 10, 20, 0, 0).getTimeInMillis());

        TestTaskCompleteCallback secondCallback = new TestTaskCompleteCallback();
        repository.completeTask((int) taskId, (int) userId, secondCallback);

        User userAfterSecond = userDao.getUserById((int) userId);
        Task taskAfterSecond = taskDao.getTaskById((int) taskId);
        int historyAfterSecond = taskCompletionDao.countCompletionsForTask((int) taskId);

        assertTrue(firstCallback.successCalled);
        assertTrue(secondCallback.successCalled);
        assertFalse(secondCallback.errorCalled);

        assertFalse(taskAfterFirst.isCompleted());
        assertFalse(taskAfterSecond.isCompleted());

        assertEquals(userAfterFirst.getExperience() + 10, userAfterSecond.getExperience());
        assertEquals(userAfterFirst.getBerries() + 5, userAfterSecond.getBerries());
        assertEquals(userAfterFirst.getEcoCoins(), userAfterSecond.getEcoCoins());

        assertEquals(1, historyAfterFirst);
        assertEquals(2, historyAfterSecond);

        assertEquals(fixedNow.get(), taskAfterSecond.getLastCompletedAt());
    }

    @Test
    public void completeTask_taskDoesNotBelongToUser_returnsError() {
        fixedNow.set(calendarOf(2026, Calendar.APRIL, 4, 10, 20, 0, 0).getTimeInMillis());

        User owner = new User("owner", "hashed");
        long ownerId = userDao.insertUser(owner);

        User intruder = new User("intruder", "hashed");
        long intruderId = userDao.insertUser(intruder);

        Task task = new Task(
                (int) ownerId,
                "Salir",
                "Descripcion",
                Task.CATEGORY_GENERAL,
                20,
                10,
                0,
                Task.DIFFICULTY_MEDIUM,
                Task.FREQUENCY_ONCE,
                false
        );
        long taskId = taskDao.insertTask(task);

        TestTaskCompleteCallback callback = new TestTaskCompleteCallback();
        repository.completeTask((int) taskId, (int) intruderId, callback);

        User ownerAfter = userDao.getUserById((int) ownerId);
        User intruderAfter = userDao.getUserById((int) intruderId);
        Task taskAfter = taskDao.getTaskById((int) taskId);

        assertFalse(callback.successCalled);
        assertTrue(callback.errorCalled);
        assertNotNull(callback.errorMessage);

        assertEquals(0, ownerAfter.getExperience());
        assertEquals(0, ownerAfter.getBerries());
        assertEquals(0, ownerAfter.getEcoCoins());

        assertEquals(0, intruderAfter.getExperience());
        assertEquals(0, intruderAfter.getBerries());
        assertEquals(0, intruderAfter.getEcoCoins());

        assertFalse(taskAfter.isCompleted());
        assertEquals(0L, taskAfter.getLastCompletedAt());

        assertEquals(0, taskCompletionDao.countCompletionsForTask((int) taskId));
    }

    private Calendar calendarOf(int year, int month, int day, int hour, int minute, int second, int millis) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, millis);
        return cal;
    }

    private static class TestTaskCompleteCallback implements MainRepository.TaskCompleteCallback {
        boolean successCalled;
        boolean errorCalled;
        int rewardXP;
        int rewardBerries;
        int ecoReward;
        boolean leveledUp;
        String errorMessage;

        @Override
        public void onSuccess(int rewardXP, int rewardBerries, int ecoReward, boolean leveledUp) {
            this.successCalled = true;
            this.rewardXP = rewardXP;
            this.rewardBerries = rewardBerries;
            this.ecoReward = ecoReward;
            this.leveledUp = leveledUp;
        }

        @Override
        public void onError(String message) {
            this.errorCalled = true;
            this.errorMessage = message;
        }
    }
}