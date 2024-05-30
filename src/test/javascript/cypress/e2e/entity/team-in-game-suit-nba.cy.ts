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

describe('TeamInGame e2e test', () => {
  const teamInGamePageUrl = '/team-in-game-suit-nba';
  const teamInGamePageUrlPattern = new RegExp('/team-in-game-suit-nba(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const teamInGameSample = {};

  let teamInGame;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/team-in-games+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/team-in-games').as('postEntityRequest');
    cy.intercept('DELETE', '/api/team-in-games/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (teamInGame) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/team-in-games/${teamInGame.id}`,
      }).then(() => {
        teamInGame = undefined;
      });
    }
  });

  it('TeamInGames menu should load TeamInGames page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('team-in-game-suit-nba');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TeamInGame').should('exist');
    cy.url().should('match', teamInGamePageUrlPattern);
  });

  describe('TeamInGame page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(teamInGamePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TeamInGame page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/team-in-game-suit-nba/new$'));
        cy.getEntityCreateUpdateHeading('TeamInGame');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInGamePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/team-in-games',
          body: teamInGameSample,
        }).then(({ body }) => {
          teamInGame = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/team-in-games+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/team-in-games?page=0&size=20>; rel="last",<http://localhost/api/team-in-games?page=0&size=20>; rel="first"',
              },
              body: [teamInGame],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(teamInGamePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TeamInGame page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('teamInGame');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInGamePageUrlPattern);
      });

      it('edit button click should load edit TeamInGame page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TeamInGame');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInGamePageUrlPattern);
      });

      it('edit button click should load edit TeamInGame page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TeamInGame');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInGamePageUrlPattern);
      });

      it('last delete button click should delete instance of TeamInGame', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('teamInGame').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', teamInGamePageUrlPattern);

        teamInGame = undefined;
      });
    });
  });

  describe('new TeamInGame page', () => {
    beforeEach(() => {
      cy.visit(`${teamInGamePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TeamInGame');
    });

    it('should create an instance of TeamInGame', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        teamInGame = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', teamInGamePageUrlPattern);
    });
  });
});
