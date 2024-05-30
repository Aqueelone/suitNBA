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

describe('TeamInSeason e2e test', () => {
  const teamInSeasonPageUrl = '/team-in-season-suit-nba';
  const teamInSeasonPageUrlPattern = new RegExp('/team-in-season-suit-nba(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const teamInSeasonSample = {};

  let teamInSeason;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/team-in-seasons+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/team-in-seasons').as('postEntityRequest');
    cy.intercept('DELETE', '/api/team-in-seasons/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (teamInSeason) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/team-in-seasons/${teamInSeason.id}`,
      }).then(() => {
        teamInSeason = undefined;
      });
    }
  });

  it('TeamInSeasons menu should load TeamInSeasons page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('team-in-season-suit-nba');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TeamInSeason').should('exist');
    cy.url().should('match', teamInSeasonPageUrlPattern);
  });

  describe('TeamInSeason page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(teamInSeasonPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TeamInSeason page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/team-in-season-suit-nba/new$'));
        cy.getEntityCreateUpdateHeading('TeamInSeason');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInSeasonPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/team-in-seasons',
          body: teamInSeasonSample,
        }).then(({ body }) => {
          teamInSeason = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/team-in-seasons+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/team-in-seasons?page=0&size=20>; rel="last",<http://localhost/api/team-in-seasons?page=0&size=20>; rel="first"',
              },
              body: [teamInSeason],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(teamInSeasonPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TeamInSeason page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('teamInSeason');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInSeasonPageUrlPattern);
      });

      it('edit button click should load edit TeamInSeason page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TeamInSeason');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInSeasonPageUrlPattern);
      });

      it('edit button click should load edit TeamInSeason page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TeamInSeason');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInSeasonPageUrlPattern);
      });

      it('last delete button click should delete instance of TeamInSeason', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('teamInSeason').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInSeasonPageUrlPattern);

        teamInSeason = undefined;
      });
    });
  });

  describe('new TeamInSeason page', () => {
    beforeEach(() => {
      cy.visit(`${teamInSeasonPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TeamInSeason');
    });

    it('should create an instance of TeamInSeason', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        teamInSeason = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', teamInSeasonPageUrlPattern);
    });
  });
});
