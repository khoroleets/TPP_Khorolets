package travel.Repository;

import org.springframework.data.repository.ListCrudRepository;

import travel.Model.TravelType;

public interface TravelTypeRepository extends ListCrudRepository<TravelType, Integer> {
	
}