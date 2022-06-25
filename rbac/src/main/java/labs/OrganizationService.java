package labs;
 
import java.util.List;
 
import javax.transaction.Transactional;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
@Service
@Transactional
public class OrganizationService {
 
    @Autowired
    private OrganizationRepository repo;
     
    public List<Organization> listAll() {
        return repo.findAll();
    }
     
    public void save(Organization org) {
        repo.save(org);
    }
     
    public Organization get(Integer id) {
        return repo.findById(id).get();
    }
     
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}