package entities;

import enums.UserType;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

public class QuestionsEntity implements Entity{
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private UserType role;
    private Integer userId;
    private Integer requestsId;
    private Boolean answer;
    private Date time = new Date();
    private Integer partnerId;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }

    public Integer getRequestsId() {
        return requestsId;
    }

    public void setRequestsId(Integer requestsId) {
        this.requestsId = requestsId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserType getRole() {
        return role;
    }

    public void setRole(UserType role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Integer partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionsEntity)) return false;
        QuestionsEntity that = (QuestionsEntity) o;
        return id.equals(that.id) &&
                role == that.role &&
                userId.equals(that.userId) &&
                requestsId.equals(that.requestsId) &&
                Objects.equals(answer, that.answer) &&
                time.equals(that.time) &&
                partnerId.equals(that.partnerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, userId, requestsId, answer, time, partnerId);
    }

    @Override
    public String toString() {
        return "QuestionsEntity{" +
                "id=" + id +
                ", role=" + role +
                ", userId=" + userId +
                ", requestsId=" + requestsId +
                ", answer=" + answer +
                ", time=" + time +
                ", partnerId=" + partnerId +
                '}';
    }
}
