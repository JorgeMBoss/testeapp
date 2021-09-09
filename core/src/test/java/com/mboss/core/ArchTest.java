package com.mboss.core;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.mboss.core");

        noClasses()
            .that()
            .resideInAnyPackage("com.mboss.core.service..")
            .or()
            .resideInAnyPackage("com.mboss.core.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.mboss.core.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
