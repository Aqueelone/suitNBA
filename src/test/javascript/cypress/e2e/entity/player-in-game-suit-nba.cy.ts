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

describe('PlayerInGame e2e test', () => {
  const playerInGamePageUrl = '/player-in-game-suit-nba';
  const playerInGamePageUrlPattern = new RegExp('/player-in-game-suit-nba(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const playerInGameSample = { points: 10008 };

  let playerInGame;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/player-in-games+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/player-in-games').as('postEntityRequest');
    cy.intercept('DELETE', '/api/player-in-games/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (playerInGame) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/player-in-games/${playerInGame.id}`,
      }).then(() => {
        playerInGame = undefined;
      });
    }
  });

  it('PlayerInGames menu should load PlayerInGames page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('player-in-game-suit-nba');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PlayerInGame').should('exist');
    cy.url().should('match', playerInGamePageUrlPattern);
  });

  describe('PlayerInGame page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(playerInGamePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PlayerInGame page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/player-in-game-suit-nba/new$'));
        cy.getEntityCreateUpdateHeading('PlayerInGame');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInGamePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/player-in-games',
          body: playerInGameSample,
        }).then(({ body }) => {
          playerInGame = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/player-in-games+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/player-in-games?page=0&size=20>; rel="last",<http://localhost/api/player-in-games?page=0&size=20>; rel="first"',
              },
              body: [playerInGame],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(playerInGamePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PlayerInGame page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('playerInGame');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInGamePageUrlPattern);
      });

      it('edit button click should load edit PlayerInGame page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PlayerInGame');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInGamePageUrlPattern);
      });

      it('edit button click should load edit PlayerInGame page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PlayerInGame');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInGamePageUrlPattern);
      });

      it('last delete button click should delete instance of PlayerInGame', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('playerInGame').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', playerInGamePageUrlPattern);

        playerInGame = undefined;
      });
    });
  });

  describe('new PlayerInGame page', () => {
    beforeEach(() => {
      cy.visit(`${playerInGamePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PlayerInGame');
    });

    it('should create an instance of PlayerInGame', () => {
      cy.get(`[data-cy="points"]`).type('15344');
      cy.get(`[data-cy="points"]`).should('have.value', '15344');

      cy.get(`[data-cy="rebounds"]`).type('11203');
      cy.get(`[data-cy="rebounds"]`).should('have.value', '11203');

      cy.get(`[data-cy="assists"]`).type('11978');
      cy.get(`[data-cy="assists"]`).should('have.value', '11978');

      cy.get(`[data-cy="steals"]`).type('17982');
      cy.get(`[data-cy="steals"]`).should('have.value', '17982');

      cy.get(`[data-cy="blocks"]`).type('7391');
      cy.get(`[data-cy="blocks"]`).should('have.value', '7391');

      cy.get(`[data-cy="fouls"]`).type('30425');
      cy.get(`[data-cy="fouls"]`).should('have.value', '30425');

      cy.get(`[data-cy="turnovers"]`).type('20490');
      cy.get(`[data-cy="turnovers"]`).should('have.value', '20490');

      cy.get(`[data-cy="played"]`).type('1578.3');
      cy.get(`[data-cy="played"]`).should('have.value', '1578.3');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        playerInGame = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', playerInGamePageUrlPattern);
    });
  });
});
