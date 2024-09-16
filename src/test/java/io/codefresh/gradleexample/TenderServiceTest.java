package io.codefresh.gradleexample;

import io.codefresh.gradleexample.business.service.tenders.TenderResponsibleServiceInterface;
import io.codefresh.gradleexample.business.service.validators.values.impl.ValidationService;
import io.codefresh.gradleexample.dao.builders.tender.TenderBuilder;
import io.codefresh.gradleexample.dao.entities.tenders.ServiceTypes;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.repository.tenders.TenderRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TenderServiceTest {
    @Mock
    private TenderRepository tenderRepository;

    private TenderBuilder tenderBuilder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        tenderBuilder = new TenderBuilder();
    }


    @Test
    public void TenderCreationTestSuccess(){
        String name = "Tender Name";
        String description = "Tender Description";
        ServiceTypes serviceType = ServiceTypes.Construction;
        UUID organization_id = UUID.randomUUID();
        String creatorUsername = "creatorUser";
        UUID tenderID = UUID.randomUUID();


        tenderBuilder.name(name).description(description).serviceType(serviceType).organization_id(organization_id).creatorName(creatorUsername);
        Tender tender = tenderBuilder.Build();
        tender.setId(tenderID);

        when(tenderRepository.save(any(Tender.class))).thenReturn(tender);
        when(tenderRepository.findById(tenderID)).thenReturn(Optional.of(tender));

        tenderRepository.save(tender);

        Optional<Tender> optionalTender = tenderRepository.findById(tenderID);
        System.out.println("optionalTender = " + optionalTender);
        Assert.assertTrue("OK", optionalTender.isPresent());
    }
}
