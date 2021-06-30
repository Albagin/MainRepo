package entities;

import enums.UserType;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Objects;

public class ScoresEntity implements Entity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    private Integer score;
    private UserType role;
    private Integer userId;
    private Integer courseId;

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public UserType getRole() {
        return role;
    }

    public void setRole(UserType role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScoresEntity)) return false;
        ScoresEntity that = (ScoresEntity) o;
        return id.equals(that.id) &&
                score.equals(that.score) &&
                role.equals(that.role) &&
                userId.equals(that.userId) &&
                courseId.equals(that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, score, role, userId, courseId);
    }

    @Override
    public String toString() {
        return "ScoresEntity{" +
                "id=" + id +
                ", score='" + score + '\'' +
                ", role=" + role +
                ", userId=" + userId +
                ", courseId=" + courseId +
                '}';
    }
}
