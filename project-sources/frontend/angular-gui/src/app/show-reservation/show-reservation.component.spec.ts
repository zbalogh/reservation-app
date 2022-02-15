import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowReservationComponent } from './show-reservation.component';

describe('ShowReservationComponent', () => {
  let component: ShowReservationComponent;
  let fixture: ComponentFixture<ShowReservationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShowReservationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowReservationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
