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

describe('PlayerInTeam e2e test', () => {
  const playerInTeamPageUrl = '/player-in-team';
  const playerInTeamPageUrlPattern = new RegExp('/player-in-team(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const playerInTeamSample = {};

  let playerInTeam;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/player-in-teams+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/player-in-teams').as('postEntityRequest');
    cy.intercept('DELETE', '/api/player-in-teams/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (playerInTeam) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/player-in-teams/${playerInTeam.id}`,
      }).then(() => {
        playerInTeam = undefined;
      });
    }
  });

  it('PlayerInTeams menu should load PlayerInTeams page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('player-in-team');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PlayerInTeam').should('exist');
    cy.url().should('match', playerInTeamPageUrlPattern);
  });

  describe('PlayerInTeam page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(playerInTeamPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PlayerInTeam page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/player-in-team/new$'));
        cy.getEntityCreateUpdateHeading('PlayerInTeam');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInTeamPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/player-in-teams',
          body: playerInTeamSample,
        }).then(({ body }) => {
          playerInTeam = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/player-in-teams+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/player-in-teams?page=0&size=20>; rel="last",<http://localhost/api/player-in-teams?page=0&size=20>; rel="first"',
              },
              body: [playerInTeam],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(playerInTeamPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PlayerInTeam page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('playerInTeam');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInTeamPageUrlPattern);
      });

      it('edit button click should load edit PlayerInTeam page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PlayerInTeam');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInTeamPageUrlPattern);
      });

      it('edit button click should load edit PlayerInTeam page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PlayerInTeam');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInTeamPageUrlPattern);
      });

      it('last delete button click should delete instance of PlayerInTeam', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('playerInTeam').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInTeamPageUrlPattern);

        playerInTeam = undefined;
      });
    });
  });

  describe('new PlayerInTeam page', () => {
    beforeEach(() => {
      cy.visit(`${playerInTeamPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PlayerInTeam');
    });

    it('should create an instance of PlayerInTeam', () => {
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        playerInTeam = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', playerInTeamPageUrlPattern);
    });
  });
});
