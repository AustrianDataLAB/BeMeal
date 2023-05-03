import {Component, OnInit} from '@angular/core';
import {SelfService} from "../../services/self.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";

@Component({
  selector: 'app-show-league',
  templateUrl: './show-league.component.html',
  styleUrls: ['./show-league.component.scss']
})
export class ShowLeagueComponent implements OnInit{

    leagueId: number | null;

    constructor(private selfService: SelfService, private router: Router, private leagueService: LeagueService, private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.getLeagueId();
    }

    getLeagueId(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id !== null) {
            this.leagueId = parseInt(id);
        } else {
            // todo show error
        }
        console.log(`league id is: ${id}`);
    }


}
