package run.mycode.commit.persistence.service;

import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mycode.commit.persistence.model.Assignment;
import run.mycode.commit.persistence.model.Course;
import run.mycode.commit.persistence.model.GitHubUser;
import run.mycode.commit.persistence.repository.AssignmentRepository;

@Service
public class AssignmentService implements IAssignmentService {
    @Autowired
    private AssignmentRepository assignmentRepo;
    
    @Override
    public Assignment findById(Long id) {
        return assignmentRepo.findById(id).orElse(null);
    }
    
    @Override
    public Set<Assignment> findByCourse(Course course) {
        return assignmentRepo.findByCourse(course);
    }

    @Override
    public Set<Assignment> findByOwner(GitHubUser owner) {
        return assignmentRepo.findByOwner(owner);
    }

    @Transactional
    @Override
    public Assignment update(Assignment a) {
        return assignmentRepo.save(a);
    }

    @Transactional
    @Override
    public Assignment createAssignment(String name, GitHubUser owner, Course course) {
        Assignment a = new Assignment();
        a.setName(name);
        a = assignmentRepo.save(a);
        
        assignmentRepo.setOwnerCourse(a.getId(), owner.getId(), course.getKey());
    
        return assignmentRepo.findById(a.getId()).orElse(null);
    }

}
