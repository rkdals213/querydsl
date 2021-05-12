package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.*;
import study.querydsl.entity.*;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {
    @Autowired
    EntityManager em;

    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);
        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = new QHello("h");

        Hello result = query
                .selectFrom(qHello)
                .fetchOne();

        Assertions.assertThat(result).isEqualTo(hello);
    }

    @Test
    void test() {
        QTeam team = QTeam.team;
        QMember member = QMember.member;
        JPAQueryFactory query = new JPAQueryFactory(em);
        List<Team> result = query.selectFrom(team)
                .join(team.members, member).fetchJoin()
                .where(member.age.eq(10))
                .fetch();

        result.forEach(t -> {
            t.getMembers().forEach(m -> {
                Assertions.assertThat(m.getAge()).isEqualTo(10);
            });
        });
    }

    @Test
    void test2() {
        QTeam team = QTeam.team;
        QMember member = QMember.member;
        JPAQueryFactory query = new JPAQueryFactory(em);
        List<Team> result = query.selectFrom(team)
                .join(team.members, member)
                .where(member.age.eq(10))
                .fetch();

        result.forEach(t -> {
            t.getMembers().forEach(m -> {
                Assertions.assertThat(m.getAge()).isEqualTo(10);
            });
        });
    }

    @Test
    void test3() {
        QTeam team = QTeam.team;
        QMember member = QMember.member;
        JPAQueryFactory query = new JPAQueryFactory(em);

        List<Team> result = query.selectFrom(team)
                .join(team.members, member)
                .where(member.age.eq(10))
                .fetch();

        List<Long> teamIds = result.stream().map(Team::getId).collect(Collectors.toList());

        List<Member> members = query.selectFrom(member)
                .where(
                        member.team.id.in(teamIds),
                        member.age.eq(10)
                )
                .fetch();

        Map<Long, List<Member>> memberMap = members.stream().collect(Collectors.groupingBy(m -> m.getTeam().getId()));
        result.forEach(t -> t.setMembers(memberMap.get(t.getId())));

        result.forEach(t -> {
            t.getMembers().forEach(m -> {
                Assertions.assertThat(m.getAge()).isEqualTo(10);
            });
        });
    }

    @Test
    void test4() {
        QTeam team = QTeam.team;
        QMember member = QMember.member;
        JPAQueryFactory query = new JPAQueryFactory(em);

        List<Team> result = query.selectFrom(team)
                .fetch();

        List<Long> teamIds = result.stream().map(Team::getId).collect(Collectors.toList());

        List<Member> members = query.selectFrom(member)
                .where(
                        member.team.id.in(teamIds),
                        member.age.eq(10)
                )
                .fetch();

        Map<Long, List<Member>> memberMap = members.stream().collect(Collectors.groupingBy(m -> m.getTeam().getId()));
        result.forEach(t -> t.setMembers(memberMap.get(t.getId())));

        result.forEach(t -> {
            System.out.println(t);
            t.getMembers().forEach(m -> {
                Assertions.assertThat(m.getAge()).isEqualTo(10);
            });
        });
    }

    @Test
    void test5() {
        QTeam team = QTeam.team;
        QMember member = QMember.member;
        JPAQueryFactory query = new JPAQueryFactory(em);

        List<TeamMemberDto> result = query.select(
                new QTeamMemberDto(
                        team.id,
                        team.name
                ))
                .from(team)
                .join(team.members, member)
                .where(member.age.eq(10))
                .fetch();

        List<Long> teamIds = result.stream().map(TeamMemberDto::getTeamId).collect(Collectors.toList());

        List<MemberTeamDto> members = query.select(
                new QMemberTeamDto(
                        member.id,
                        member.username,
                        member.age,
                        member.team.id,
                        member.team.name
                ))
                .from(member)
                .where(
                        member.team.id.in(teamIds),
                        member.age.eq(10)
                )
                .fetch();

        Map<Long, List<MemberTeamDto>> memberMap = members.stream().collect(Collectors.groupingBy(MemberTeamDto::getTeamId));
        result.forEach(t -> t.setMembers(memberMap.get(t.getTeamId())));

        result.forEach(t -> {
            t.getMembers().forEach(m -> {
                Assertions.assertThat(m.getAge()).isEqualTo(10);
            });
        });
    }

}
