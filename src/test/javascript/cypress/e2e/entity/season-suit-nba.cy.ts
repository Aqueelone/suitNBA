import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Season e2e test', () => {
  const seasonPageUrl = '/season-suit-nba';
  const seasonPageUrlPattern = new RegExp('/season-suit-nba(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const seasonSample = { seasonName: 'moustache meh' };

  let season;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/seasons+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/seasons').as('postEntityRequest');
    cy.intercept('DELETE', '/api/seasons/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (season) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/seasons/${season.id}`,
      }).then(() => {
        season = undefined;
      });
    }
  });

  it('Seasons menu should load Seasons page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('season-suit-nba');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Season').should('exist');
    cy.url().should('match', seasonPageUrlPattern);
  });

  describe('Season page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(seasonPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Season page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/season-suit-nba/new$'));
        cy.getEntityCreateUpdateHeading('Season');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', seasonPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/seasons',
          body: seasonSample,
        }).then(({ body }) => {
          season = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/seasons+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/seasons?page=0&size=20>; rel="last",<http://localhost/api/seasons?page=0&size=20>; rel="first"',
              },
              body: [season],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(seasonPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Season page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('season');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', seasonPageUrlPattern);
      });

      it('edit button click should load edit Season page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Season');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', seasonPageUrlPattern);
      });

      it('edit button click should load edit Season page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Season');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', seasonPageUrlPattern);
      });

      it('last delete button click should delete instance of Season', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('season').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', seasonPageUrlPattern);

        season = undefined;
      });
    });
  });

  describe('new Season page', () => {
    beforeEach(() => {
      cy.visit(`${seasonPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Season');
    });

    it('should create an instance of Season', () => {
      cy.get(`[data-cy="seasonName"]`).type('even');
      cy.get(`[data-cy="seasonName"]`).should('have.value', 'even');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        season = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', seasonPageUrlPattern);
    });
  });
});
