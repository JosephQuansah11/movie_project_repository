package joe.amethyst.backend_tutorials.demo.solid_principles.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMovieHistoryResponse {
    private boolean success;
    private String message;
    private Long userId;
    private List<UserMovieHistoryItem> items = new ArrayList<>();
    private Integer count;
    private Long deletedHistoryId;
    private Integer archivedCount;

    public static UserMovieHistoryResponse success(Long userId, String message, List<UserMovieHistoryItem> items) {
        UserMovieHistoryResponse response = new UserMovieHistoryResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setUserId(userId);
        response.setItems(items == null ? new ArrayList<>() : items);
        response.setCount(response.getItems().size());
        return response;
    }

    public static UserMovieHistoryResponse deleted(Long userId, String message, Long deletedHistoryId) {
        UserMovieHistoryResponse response = new UserMovieHistoryResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setUserId(userId);
        response.setDeletedHistoryId(deletedHistoryId);
        response.setItems(new ArrayList<>());
        response.setCount(0);
        return response;
    }

    public static UserMovieHistoryResponse archived(Long userId, String message, List<UserMovieHistoryItem> items) {
        UserMovieHistoryResponse response = success(userId, message, items);
        response.setArchivedCount(items == null ? 0 : items.size());
        return response;
    }
}
