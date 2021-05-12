package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamMemberDto {
    private Long teamId;
    private String teamName;
    private List<MemberTeamDto> members = new ArrayList<>();

    @QueryProjection
    public TeamMemberDto(Long teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
