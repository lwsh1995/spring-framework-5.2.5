package spring.context.annotation.config;

import org.springframework.context.annotation.Import;
import spring.context.annotation.component.SpringImportSelector;

@Import(SpringImportSelector.class)
public class ImportConfig {
}
