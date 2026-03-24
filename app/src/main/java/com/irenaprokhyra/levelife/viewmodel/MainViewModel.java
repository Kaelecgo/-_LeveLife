package com.irenaprokhyra.levelife.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.irenaprokhyra.levelife.model.MainRepository;
import com.irenaprokhyra.levelife.model.User;
import com.irenaprokhyra.levelife.model.Task;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final MainRepository repository;
    private LiveData<User> user;
    private LiveData<List<Task>> userTasks;

    public MainViewModel(Application application) {
        super(application);
        repository = MainRepository.getInstance(application);
    }

    public void init(int userId) {
        if (this.user == null) {
            user = repository.getUserLiveData(userId);
            userTasks = repository.getTasksLiveData(userId);
        }
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<List<Task>> getUserTasks() { return userTasks; }

    public void completeTask(Task task, User currentUser) {
        task.setCompleted(true);
        currentUser.addExperience(task.getRewardXP());
        currentUser.addBerries(task.getRewardBerries());
        
        repository.updateUser(currentUser);
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public void insertTask(Task task) {
        repository.insertTask(task);
    }
}